package com.thoughtworks.i0.jetty.internal;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ContainerCreator;
import com.thoughtworks.i0.core.ServletContainer;
import com.thoughtworks.i0.jetty.EmbeddedJetty;

public class EmbeddedFactory implements ContainerCreator<EmbeddedJetty, Configuration> {
    @Override
    public ServletContainer create(EmbeddedJetty annotation, Configuration configuration) {
        return new Embedded(configuration.getHttp());
    }
}
