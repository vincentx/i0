package com.thoughtworks.i0.jetty.internal;

import com.google.inject.Binder;
import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.BindingProvider;
import com.thoughtworks.i0.core.ContainerCreator;
import com.thoughtworks.i0.core.ServletContainer;
import com.thoughtworks.i0.core.internal.servlet.AssetServlet;
import com.thoughtworks.i0.jetty.EmbeddedJetty;

public class EmbeddedFactory implements ContainerCreator<EmbeddedJetty, Configuration>, BindingProvider<EmbeddedJetty, Configuration> {
    @Override
    public ServletContainer create(EmbeddedJetty annotation, Configuration configuration) {
        return new Embedded(configuration.getHttp());
    }

    @Override
    public void configure(Binder binder, final EmbeddedJetty annotation, ApplicationModule<?> module, Configuration configuration) {
        if (annotation.assets().length == 0) return;
        binder.install(new ServletModule() {
            @Override
            protected void configureServlets() {
                for (EmbeddedJetty.Asset asset : annotation.assets()) {
                    serve(asset.uri() + "/*").with(new AssetServlet(asset.resource()));
                }
            }
        });
    }
}
