package com.thoughtworks.i0.core;

import com.google.inject.Binder;

import java.lang.annotation.Annotation;

public interface BindingConfigurator<AnnotationType extends Annotation, ConfigurationType> extends FacetEnabler {
    void configure(Binder binder, AnnotationType annotation, ConfigurationType configuration);
}
