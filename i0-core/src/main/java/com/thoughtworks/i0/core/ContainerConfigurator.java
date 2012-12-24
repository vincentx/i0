package com.thoughtworks.i0.core;

import java.lang.annotation.Annotation;

public interface ContainerConfigurator<AnnotationType extends Annotation, ConfigurationType> extends FacetEnabler {
    void configure(ServletContainer container, AnnotationType annotation, ConfigurationType configuration);
}
