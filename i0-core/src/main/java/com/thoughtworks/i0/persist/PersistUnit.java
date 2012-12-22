package com.thoughtworks.i0.persist;

import com.thoughtworks.i0.facet.Facet;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Facet(PersistUnitEnabler.class)
public @interface PersistUnit {
    String value();
}
