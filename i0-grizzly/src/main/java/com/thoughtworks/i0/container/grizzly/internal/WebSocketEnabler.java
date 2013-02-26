package com.thoughtworks.i0.container.grizzly.internal;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.container.grizzly.WebSocket;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.ContainerConfigurator;
import com.thoughtworks.i0.core.internal.util.ClassScanner;
import org.glassfish.tyrus.*;
import org.glassfish.tyrus.server.TyrusServerConfiguration;
import org.glassfish.tyrus.spi.SPIRegisteredEndpoint;
import org.glassfish.tyrus.spi.TyrusServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.websocket.*;
import javax.websocket.server.ServerApplicationConfiguration;
import javax.websocket.server.WebSocketEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.i0.core.internal.util.TypePredicates.isPresent;

public class WebSocketEnabler implements ContainerConfigurator<WebSocket, Configuration, Embedded> {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationModule.class);

    @Override
    public void configure(Embedded container, WebSocket annotation, ApplicationModule<Configuration> module) throws DeploymentException, IOException {
        final String[] autoScanPackages = new String[]{module.getClass().getPackage().getName()};
        ClassScanner scanner = new ClassScanner(annotation.packages().length == 0 ? autoScanPackages : annotation.packages());
        final Set<Class<?>> endpoints = scanner.findBy(isPresent(WebSocketEndpoint.class));
        if (logger.isInfoEnabled())
            logger.info(endpoints.isEmpty() ? "No WebSocket endpoint classes found." : "WebSocket endpoint classes found:\n  {}",
                    on("\n  ").join(transform(endpoints, LOG_FORMATTER)));
        if (!endpoints.isEmpty()) {
            Injector injector = container.injector().createChildInjector(new AbstractModule() {

                @Override
                protected void configure() {
                    for (Class<?> endpointClass : endpoints)
                        bind(endpointClass);
                }
            });
            TyrusServerContainer tyrusServerContainer = new TyrusServerContainer(container, module.path(), endpoints, injector);
            tyrusServerContainer.start();
        }
    }

    public class TyrusServerContainer extends WithProperties implements WebSocketContainer {
        private final Embedded server;
        private final String contextPath;
        private final ServerApplicationConfiguration configuration;
        private final Set<SPIRegisteredEndpoint> endpoints = new HashSet<SPIRegisteredEndpoint>();
        private final ErrorCollector collector;
        private final ComponentProviderService componentProvider;
        private final Injector injector;

        private long maxSessionIdleTimeout = 0;
        private long defaultAsyncSendTimeout = 0;
        private int maxTextMessageBufferSize = 0;
        private int maxBinaryMessageBufferSize = 0;

        /**
         * Create new {@link TyrusServerContainer}.
         *
         * @param server      underlying server.
         * @param contextPath context path of current application.
         * @param classes     classes to be included in this application instance. Can contain any combination of annotated
         *                    endpoints (see {@link javax.websocket.server.WebSocketEndpoint}) or {@link javax.websocket.Endpoint} descendants.
         */
        public TyrusServerContainer(final Embedded server, final String contextPath,
                                    final Set<Class<?>> classes, Injector injector) {
            this.injector = injector;
            this.collector = new ErrorCollector();
            this.server = server;
            this.contextPath = contextPath;
            this.configuration = new TyrusServerConfiguration(classes, this.collector);
            componentProvider = ComponentProviderService.create(collector);
        }

        /**
         * Start container.
         *
         * @throws java.io.IOException when any IO related issues emerge during {@link org.glassfish.tyrus.spi.TyrusServer#start()}.
         * @throws DeploymentException when any deployment related error is found; should contain list of all found issues.
         */
        public void start() throws IOException, DeploymentException {
            try {
                for (Class<?> endpointClass : configuration.getAnnotatedEndpointClasses(null)) {
                    AnnotatedEndpoint endpoint = AnnotatedEndpoint.fromInstance(injector.getInstance(endpointClass), componentProvider, true, collector);
                    EndpointConfiguration config = endpoint.getEndpointConfiguration();
                    EndpointWrapper ew = new EndpointWrapper(endpoint, config, componentProvider, this, contextPath, collector);
                    deploy(ew);
                }

            } catch (DeploymentException de) {
                collector.addException(de);
            }

            if (!collector.isEmpty()) {
                throw collector.composeComprehensiveException();
            }
        }

        private void deploy(EndpointWrapper wrapper) {
            SPIRegisteredEndpoint ge = server.register(wrapper);
            endpoints.add(ge);
        }

        /**
         * Undeploy all endpoints and stop underlying {@link TyrusServer}.
         */
        public void stop() {
            for (SPIRegisteredEndpoint wsa : this.endpoints) {
                wsa.remove();
                this.server.unregister(wsa);
                java.util.logging.Logger.getLogger(getClass().getName()).info("Closing down : " + wsa);
            }
        }

        @Override
        public Session connectToServer(Class annotatedEndpointClass, URI path) throws DeploymentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Session connectToServer(Class<? extends Endpoint> endpointClass, ClientEndpointConfiguration cec, URI path) throws DeploymentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getMaxSessionIdleTimeout() {
            return maxSessionIdleTimeout;
        }

        @Override
        public void setMaxSessionIdleTimeout(long timeout) {
            this.maxSessionIdleTimeout = timeout;
        }

        @Override
        public int getDefaultMaxBinaryMessageBufferSize() {
            return maxBinaryMessageBufferSize;
        }

        @Override
        public void setDefaultMaxBinaryMessageBufferSize(int max) {
            this.maxBinaryMessageBufferSize = max;
        }

        @Override
        public int getDefaultMaxTextMessageBufferSize() {
            return maxTextMessageBufferSize;
        }

        @Override
        public void setDefaultMaxTextMessageBufferSize(int max) {
            this.maxTextMessageBufferSize = max;
        }

        @Override
        public Set<Extension> getInstalledExtensions() {
            return Collections.emptySet();
        }

        @Override
        public long getDefaultAsyncSendTimeout() {
            return defaultAsyncSendTimeout;
        }

        @Override
        public void setAsyncSendTimeout(long timeoutmillis) {
            defaultAsyncSendTimeout = timeoutmillis;
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
