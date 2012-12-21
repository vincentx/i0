package com.thoughtworks.i0;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Application {
    String name();

    boolean autoScanning() default true;

    String api() default "/api/*";
}
