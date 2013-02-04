package com.thoughtworks.i0.core;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.thoughtworks.i0.config.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.thoughtworks.i0.config.Configuration.config;
import static com.thoughtworks.i0.core.internal.util.TypePredicates.isFacet;
import static com.thoughtworks.i0.core.internal.util.TypePredicates.isStack;
import static com.thoughtworks.i0.core.internal.util.TypePredicates.typeSubClassOf;

public abstract class ApplicationModule<T extends Configuration> extends AbstractModule {
    protected final Application application;

    private Optional<T> configuration = Optional.absent();

    private final Map<Annotation, FacetEnabler> enablers;

    public ApplicationModule() {
        checkState(getClass().isAnnotationPresent(Application.class), "missing @Application annotation for application module '" + getClass().getName() + "'");
        this.application = getClass().getAnnotation(Application.class);
        this.enablers = findEnablers();
    }

    private ImmutableMap<Annotation, FacetEnabler> findEnablers() {
        ImmutableMap.Builder<Annotation, FacetEnabler> builder = ImmutableMap.builder();
        for (Annotation annotation : filter(copyOf(getClass().getAnnotations()), isStack))
            findEnablers(annotation.annotationType(), builder);
        findEnablers(getClass(), builder);
        return builder.build();
    }

    private void findEnablers(Class<?> target, ImmutableMap.Builder<Annotation, FacetEnabler> builder) {
        for (Annotation annotation : filter(copyOf(target.getAnnotations()), isFacet)) {
            Facet facet = annotation.annotationType().getAnnotation(Facet.class);
            Class<? extends FacetEnabler> enablerClass = facet.value();
            try {
                builder.put(annotation, enablerClass.getConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new IllegalArgumentException("Can not create enabler for facet: " + facet);
            }
        }
    }

    final Class<T> configurationType() {
        return (Class<T>) find(typeParametersOf(genericSuperClass()), typeSubClassOf(Configuration.class));
    }

    private Type genericSuperClass() {
        Type t = getClass();
        while (t instanceof Class<?>) t = ((Class<?>) t).getGenericSuperclass();
        return t;
    }

    private ImmutableSet<Type> typeParametersOf(Type t) {
        return t instanceof ParameterizedType ? copyOf(((ParameterizedType) t).getActualTypeArguments()) :
                ImmutableSet.<Type>of();
    }

    @Override
    protected final void configure() {
        for (Map.Entry<Annotation, FacetEnabler> enabler : enablers.entrySet())
            if (enabler.getValue() instanceof BindingProvider)
                ((BindingProvider) enabler.getValue()).configure(binder(), enabler.getKey(), this, configuration());
    }

    void setConfiguration(Configuration configuration) {
        this.configuration = of((T) configuration);
    }

    public final T configuration() {
        if (!configuration.isPresent()) configuration = fromNullable(createDefaultConfiguration(config()));
        Preconditions.checkArgument(configuration.isPresent(), "No configuration for module: " + getClass());
        return configuration.get();
    }

    protected T createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return null;
    }

    public String path() {
        return application.value().startsWith("/") ? application.value() : "/" + application.value();
    }

    public Map<Annotation, FacetEnabler> enablers() {
        return enablers;
    }

    public ApplicationModule[] getSubModules() {
        return new ApplicationModule[0];
    }
}
