package com.thoughtworks.i0.facet;

import com.google.inject.Binder;
import com.thoughtworks.i0.config.Configuration;

import java.lang.annotation.Annotation;

public interface FacetEnabler<AnnotationType extends Annotation, ConfigurationType> {
      void createBindings(Binder binder, AnnotationType annotation, ConfigurationType configuration);

    void performPreLaunchTasks(AnnotationType annotation, ConfigurationType configuration);
}
