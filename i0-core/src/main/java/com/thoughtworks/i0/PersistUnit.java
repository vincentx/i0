package com.thoughtworks.i0;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistUnit {
    String value();
}
