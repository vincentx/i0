package com.thoughtworks.i0.jetty.internal;


import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.thoughtworks.i0.config.HttpConfiguration;
import com.thoughtworks.i0.config.util.Duration;
import com.thoughtworks.i0.core.ServletContainer;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.annotation.Nullable;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static com.google.common.collect.Iterables.toArray;
import static com.thoughtworks.i0.config.HttpConfiguration.SslConfiguration;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

public class Embedded implements ServletContainer {
    private final Server server;
    private final ImmutableMap.Builder<String, Injector> injectors = ImmutableMap.builder();

    public Embedded(HttpConfiguration configuration) {
        server = new Server(threadPool(configuration));
        server.setConnectors(configureConnectors(configuration));
    }

    @Override
    public void addServletContext(String name, boolean shareNothing, final Module... modules) {
        Preconditions.checkState(!server.isRunning(), "Server is running.");
        ServletContextHandler handler = new ServletContextHandler(server, root(name), shareNothing ? NO_SESSIONS : SESSIONS);
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        handler.addServlet(DefaultServlet.class, "/*");

        final Injector injector = Guice.createInjector(modules);
        injectors.put(name, injector);

        handler.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injector;
            }
        });
    }

    @Override
    public Injector context(String name) {
        return injectors.build().get(name);
    }

    private String root(String name) {
        return name.startsWith("/") ? name : "/" + name;
    }

    private ServerConnector[] configureConnectors(HttpConfiguration configuration) {
        return new ServerConnector[]{
                configureHttp(configuration, configureConnector(configuration))
        };
    }

    private ServerConnector configureConnector(HttpConfiguration configuration) {
        return new ServerConnector(server, null, null, null, configuration.getAcceptorThreads(), configuration.getSelectorThreads(),
                connectionFactories(configuration));
    }

    private ConnectionFactory[] connectionFactories(HttpConfiguration configuration) {
        return toArray(new ImmutableList.Builder<ConnectionFactory>()
                .addAll(configuration.getSsl().transform(toSslConnectionFactory).asSet())
                .add(new HttpConnectionFactory()).build(), ConnectionFactory.class);
    }

    private ServerConnector configureHttp(HttpConfiguration configuration, ServerConnector connector) {
        for (String host : configuration.getHost().asSet()) connector.setHost(host);
        connector.setPort(configuration.getPort());
        connector.setAcceptQueueSize(configuration.getAcceptQueueSize());
        connector.setSoLingerTime(configuration.getSoLingerTime().transform(new Function<Duration, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable Duration input) {
                return (int) input.value();
            }
        }).or(-1));
        connector.setIdleTimeout(configuration.getIdleTimeout().value());
        return connector;
    }

    private QueuedThreadPool threadPool(HttpConfiguration configuration) {
        return new QueuedThreadPool(configuration.getMaxThread(), configuration.getMinThread(), (int) configuration.getMaxIdleTime().value());
    }

    @Override
    public void start(boolean standalone) throws Exception {
        server.start();
        if (standalone) server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    private static final Function<SslConfiguration, ConnectionFactory> toSslConnectionFactory = new Function<SslConfiguration, ConnectionFactory>() {
        @Nullable
        @Override
        public ConnectionFactory apply(@Nullable SslConfiguration input) {
            return new SslConnectionFactory(configureTrustStore(input, configureKeyStore(input, new SslContextFactory())), HttpVersion.HTTP_1_1.asString());
        }

        private SslContextFactory configureTrustStore(SslConfiguration input, SslContextFactory sslContextFactory) {
            for (String trustStorePath : input.getTrustStorePath().asSet())
                sslContextFactory.setTrustStorePath(trustStorePath);
            for (String trustStorePassword : input.getTrustStorePassword().asSet())
                sslContextFactory.setTrustStorePassword(trustStorePassword);
            sslContextFactory.setTrustStoreType(input.getTrustStoreType());
            return sslContextFactory;
        }

        private SslContextFactory configureKeyStore(SslConfiguration input, SslContextFactory sslContextFactory) {
            for (String keyStorePath : input.getKeyStorePath().asSet())
                sslContextFactory.setKeyStorePath(keyStorePath);
            for (String keyStorePassword : input.getKeyStorePassword().asSet())
                sslContextFactory.setKeyStorePassword(keyStorePassword);
            for (String keyManagerPassword : input.getKeyManagerPassword().asSet())
                sslContextFactory.setKeyManagerPassword(keyManagerPassword);
            sslContextFactory.setKeyStoreType(input.getKeyStoreType());
            return sslContextFactory;
        }
    };
}
