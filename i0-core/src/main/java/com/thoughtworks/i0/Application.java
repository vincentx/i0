package com.thoughtworks.i0;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Application {
    String name();

    String persistUnit() default "domain";

    String root() default "";

    String[] api() default {};

    String[] servlets() default {};

    String[] filters() default {};

    String[] services() default {};
}
