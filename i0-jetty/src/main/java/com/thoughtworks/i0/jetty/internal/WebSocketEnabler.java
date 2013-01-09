package com.thoughtworks.i0.jetty.internal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Binder;
import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.BindingProvider;
import com.thoughtworks.i0.core.internal.util.ClassScanner;
import com.thoughtworks.i0.jetty.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.websocket.annotations.WebSocketEndpoint;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.i0.core.internal.util.TypePredicates.isPresent;

public class WebSocketEnabler implements BindingProvider<WebSocket, Configuration> {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationModule.class);

    @Override
    public void configure(Binder binder, WebSocket annotation, ApplicationModule<?> module, Configuration configuration) {
        final String[] autoScanPackages = new String[]{module.getClass().getPackage().getName()};
        ClassScanner scanner = new ClassScanner(annotation.packages().length == 0 ? autoScanPackages : annotation.packages());
        final Set<Class<?>> endpoints = scanner.findBy(isPresent(WebSocketEndpoint.class));
        if (logger.isInfoEnabled())
            logger.info(endpoints.isEmpty() ? "No WebSocket endpoint classes found." : "WebSocket endpoint classes found:\n  {}",
                    on("\n  ").join(transform(endpoints, LOG_FORMATTER)));
        if (!endpoints.isEmpty()) {
            binder.install(new ServletModule() {
                @Override
                protected void configureServlets() {
                    for (Class<?> endpoint : endpoints) {
                        serve(endpoint.getAnnotation(WebSocketEndpoint.class).value()).with(
                                new WebSocketEndPointServlet(), ImmutableMap.<String, String>builder()
                                .put("socketClass", endpoint.getName())
                                .build());
                    }
                }
            });
        }
    }

    public static final Function<Class<?>, String> LOG_FORMATTER = new Function<Class<?>, String>() {
        @Nullable
        @Override
        public String apply(@Nullable Class<?> input) {
            return String.format("%s --> %s", input, input.getAnnotation(WebSocketEndpoint.class).value());
        }
    };

}
