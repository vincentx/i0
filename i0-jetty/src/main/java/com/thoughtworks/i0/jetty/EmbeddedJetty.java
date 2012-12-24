package com.thoughtworks.i0.jetty;


import com.thoughtworks.i0.core.Facet;
import com.thoughtworks.i0.jetty.internal.EmbeddedFactory;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Facet(EmbeddedFactory.class)
public @interface EmbeddedJetty {
}
