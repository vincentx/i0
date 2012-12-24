package com.thoughtworks.i0.jersey;

import com.thoughtworks.i0.core.Facet;
import com.thoughtworks.i0.jersey.internal.JerseyEnabler;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Facet(JerseyEnabler.class)
public @interface RestApi {
    String prefix() default "/api*";

    String[] packages() default {};
}
