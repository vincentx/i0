package com.thoughtworks.i0.core;

import com.google.inject.Binder;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.internal.GuiceInjectionEnabler;
import com.thoughtworks.i0.core.internal.servlet.Servlet3Enabler;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.i0.config.Configuration.config;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

public class ApplicationModuleTest {
    @Test
    public void should_get_configuration_type_class() {
        assertThat(new ApplicationModuleA().configurationType(), equalTo(ConfigA.class));
    }

    @Test
    public void should_map_application_module_to_root() {
        assertThat(new ApplicationModuleA().path(), is("/a"));
    }

    @Test
    public void should_use_default_configuration_if_none_provide() {
        assertThat(new ApplicationModuleA().configuration().getHttp(), is(config().http().port(8051).build()));
    }

    @Test
    public void should_use_specified_configuration_if_provide() {
        ApplicationModuleA module = new ApplicationModuleA();
        module.setConfiguration(new ConfigA(config().http().port(8061).end().build()));
        assertThat(module.configuration().getHttp(), is(config().http().port(8061).build()));
    }

    @Test
    public void should_get_all_enabler_annotated_to_application_module() {
        Map<Annotation, FacetEnabler> enablers = new ApplicationModuleB().enablers();
        assertThat(enablers.size(), is(2));
        for (FacetEnabler enabler : enablers.values())
            assertThat(enabler, anyOf(instanceOf(GuiceInjectionEnabler.class), instanceOf(Servlet3Enabler.class)));
        for (Annotation annotation : enablers.keySet())
            assertThat(annotation.annotationType(), anyOf(equalTo(GuiceInjection.class), equalTo(Servlet3.class)));
    }

    @Test
    public void should_use_enabler_from_stack_annotation() {
        Map<Annotation, FacetEnabler> enablers = new ApplicationModuleD().enablers();
        assertThat(enablers.size(), is(2));
        for (FacetEnabler enabler : enablers.values())
            assertThat(enabler, anyOf(instanceOf(GuiceInjectionEnabler.class), instanceOf(Servlet3Enabler.class)));
        for (Annotation annotation : enablers.keySet())
            assertThat(annotation.annotationType(), anyOf(equalTo(GuiceInjection.class), equalTo(Servlet3.class)));
    }

    @Test
    public void should_add_call_all_binding_provider_to_build_module() {
        MockBinder mock = new MockBinder();

        new ApplicationModuleC().configure(mock.binder());

        assertThat(mock.instance(String.class), is("test"));
    }

    class MockBinder {
        private Map<Class<?>, AnnotatedBindingBuilder> bindings = new HashMap<>();

        private Binder binder = mock(Binder.class);

        MockBinder() {
            when(binder.bind(Matchers.<Class<?>>any())).thenAnswer(new Answer<AnnotatedBindingBuilder>() {
                @Override
                public AnnotatedBindingBuilder answer(InvocationOnMock invocation) throws Throwable {
                    AnnotatedBindingBuilder binding = mock(AnnotatedBindingBuilder.class);
                    bindings.put((Class<?>) invocation.getArguments()[0], binding);
                    return binding;
                }
            });
        }

        public Binder binder() {
            return binder;
        }

        public AnnotatedBindingBuilder key(Class<?> key) {
            return bindings.get(key);
        }

        public <T> T instance(Class<T> key) {
            ArgumentCaptor<T> instance = forClass(key);
            verify(key(key)).toInstance(instance.capture());
            return instance.getValue();
        }
    }

    @Application("a")
    private static class ApplicationModuleA extends ApplicationModule<ConfigA> {
        @Override
        protected ConfigA createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
            return new ConfigA(config().http().port(8051).end().build());
        }
    }

    private static class ConfigA extends Configuration {

        private ConfigA(Configuration configuration) {
            super(configuration);
        }
    }

    @Application("b")
    @GuiceInjection
    @Servlet3
    private static class ApplicationModuleB extends ApplicationModule<ConfigA> {

        @Override
        protected ConfigA createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
            return null;
        }
    }

    @Inherited
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Facet(BindingEnabler.class)
    private static @interface Binding {
        String value();
    }

    public static class BindingEnabler implements BindingProvider<Binding, ConfigA> {

        @Override
        public void configure(Binder binder, final Binding annotation, ApplicationModule<?> module, ConfigA configuration) {
            binder.bind(String.class).toInstance(annotation.value());
        }
    }

    @Application("c")
    @Binding("test")
    private static class ApplicationModuleC extends ApplicationModule<ConfigA> {
        @Override
        protected ConfigA createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
            return new ConfigA(config().http().end().build());
        }
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Stack
    @GuiceInjection
    @Servlet3
    private @interface MyStack {
    }

    @Application("d")
    @MyStack
    private static class ApplicationModuleD extends ApplicationModule<ConfigA> {

        @Override
        protected ConfigA createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
            return null;
        }
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Stack
    @GuiceInjection
    @Servlet3
    private @interface MyStack1 {
    }

}
