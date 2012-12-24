package com.thoughtworks.i0.core;

import com.thoughtworks.i0.config.Configuration;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.thoughtworks.i0.config.Configuration.config;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class LauncherTest {
    @Test
    public void should_call_all_startup_tasks_and_container_configer() throws Exception {
        ServletContainer container = Launcher.launch(new ApplicationModuleA(), false);
        assertThat(TaskEnabler.lastTask, is("task"));
        assertThat(ContainerFactory.lastContainer, is("container"));
        assertThat(ContainerSetter.lastContainer, is(container));
        assertThat(ContainerSetter.lastConfig, is("config"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_no_container_creator_found() throws Exception {
        Launcher.launch(new NoCreatorApplicationModule(), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_more_than_1_container_creator_found() throws Exception {
        Launcher.launch(new MoreThan1CreatorApplicationModule(), false);
    }

    @Application("name")
    @Task("task")
    @Container("container")
    @ContainerConfig("config")
    private static class ApplicationModuleA extends ApplicationModule<Configuration> {

        @Override
        protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
            return config().http().end().build();
        }
    }

    @Application("name")
    private static class NoCreatorApplicationModule extends ApplicationModule<Configuration> {

        @Override
        protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
            return config().http().end().build();
        }
    }

    @Application("name")
    @Container("container1")
    @MyStack
    private static class MoreThan1CreatorApplicationModule extends ApplicationModule<Configuration> {

        @Override
        protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
            return config().http().end().build();
        }
    }


    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Facet(TaskEnabler.class)
    private static @interface Task {
        String value();
    }

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Facet(ContainerFactory.class)
    private static @interface Container {
        String value();
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Facet(ContainerSetter.class)
    private static @interface ContainerConfig {
        String value();
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Stack
    @Container("container2")
    private @interface MyStack {
    }


    public static class TaskEnabler implements StartupTasks<Task, Configuration> {
        private static String lastTask;

        @Override
        public void perform(Task annotation, Configuration configuration) {
            lastTask = annotation.value();
        }
    }

    public static class ContainerFactory implements ContainerCreator<Container, Configuration> {
        private static String lastContainer;

        @Override
        public ServletContainer create(Container annotation, Configuration configuration) {
            lastContainer = annotation.value();
            return mock(ServletContainer.class);
        }
    }

    public static class ContainerSetter implements ContainerConfigurator<ContainerConfig, Configuration> {
        private static ServletContainer lastContainer;
        private static String lastConfig;

        @Override
        public void configure(ServletContainer container, ContainerConfig annotation, Configuration configuration) {
            lastContainer = container;
            lastConfig = annotation.value();
        }
    }
}
