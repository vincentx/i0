package com.thoughtworks.i0.core;

import com.google.inject.Binder;

import java.lang.annotation.Annotation;

public interface BindingProvider<AnnotationType extends Annotation, ConfigurationType> extends FacetEnabler {
    void configure(Binder binder, AnnotationType annotation, ApplicationModule<?> module, ConfigurationType configuration);
}
