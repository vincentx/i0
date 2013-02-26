package com.thoughtworks.i0.container.grizzly.internal;

import com.google.inject.Binder;
import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.container.grizzly.EmbeddedGrizzly;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.BindingProvider;
import com.thoughtworks.i0.core.ContainerCreator;
import com.thoughtworks.i0.core.ServletContainer;
import com.thoughtworks.i0.core.internal.servlet.AssetServlet;

public class EmbeddedFactory implements ContainerCreator<EmbeddedGrizzly, Configuration>, BindingProvider<EmbeddedGrizzly, Configuration> {

    @Override
    public ServletContainer create(EmbeddedGrizzly annotation, Configuration configuration) {
        return new Embedded(configuration.getHttp());
    }

    @Override
    public void configure(Binder binder, final EmbeddedGrizzly annotation, ApplicationModule<?> module, Configuration configuration) {
        if (annotation.assets().length == 0) return;
        binder.install(new ServletModule() {
            @Override
            protected void configureServlets() {
                for (EmbeddedGrizzly.Asset asset : annotation.assets()) {
                    serve(asset.uri() + "/*").with(new AssetServlet(asset.resource()));
                }
            }
        });
    }
}
