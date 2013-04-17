package com.thoughtworks.i0.core;

import com.thoughtworks.i0.core.internal.servlet.Servlet3Enabler;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@Target({ANNOTATION_TYPE, TYPE})
@Retention(RUNTIME)
@Facet(value = Servlet3Enabler.class, order= 1)
public @interface Servlet3 {
    String[] packages() default {};
}
