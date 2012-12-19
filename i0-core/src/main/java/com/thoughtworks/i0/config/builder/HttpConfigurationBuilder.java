package com.thoughtworks.i0.config.builder;

import com.google.common.base.Optional;
import com.thoughtworks.i0.config.HttpConfiguration;
import com.thoughtworks.i0.config.util.Duration;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.thoughtworks.i0.config.HttpConfiguration.*;

public class HttpConfigurationBuilder implements ConfigurationBuilder<HttpConfiguration> {
    private Optional<String> host = absent();
    private int port = DEFAULT_PORT;
    private int minThread = DEFAULT_MIN_THREAD;
    private int maxThread = DEFAULT_MAX_THREAD;
    private Duration maxIdleTime = DEFAULT_MAX_IDLE_TIME;
    private int acceptorThreads = DEFAULT_ACCEPTOR_THREADS;
    private int selectorThreads = DEFAULT_SELECTOR_THREADS;
    private int acceptQueueSize = DEFAULT_ACCEPT_QUEUE_SIZE;
    private Optional<Duration> soLingerTime = absent();
    private Duration idleTimeout = DEFAULT_IDLE_TIMEOUT;

    private OptionalConfigurationBuilder<SslConfigurationBuilder, SslConfiguration> ssl = new OptionalConfigurationBuilder<>(new SslConfigurationBuilder());

    public HttpConfigurationBuilder host(String host) {
        this.host = Optional.of(host);
        return this;
    }

    public HttpConfigurationBuilder port(int port) {
        this.port = port;
        return this;
    }

    public HttpConfigurationBuilder threadPool(int minThread, int maxThread) {
        this.minThread = minThread;
        this.maxThread = maxThread;
        return this;
    }


    public HttpConfigurationBuilder maxIdleTime(Duration maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    public HttpConfigurationBuilder threads(int acceptorThreads, int selectorThreads) {
        this.acceptorThreads = acceptorThreads;
        this.selectorThreads = selectorThreads;
        return this;
    }


    public HttpConfigurationBuilder acceptQueueSize(int acceptQueueSize) {
        this.acceptQueueSize = acceptQueueSize;
        return this;
    }

    public HttpConfigurationBuilder idleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    public HttpConfigurationBuilder soLingerTime(Duration soLingerTime) {
        this.soLingerTime = of(soLingerTime);
        return this;
    }

    public SslConfigurationBuilder ssl() {
        return ssl.builder();
    }

    public HttpConfiguration build() {
        return new HttpConfiguration(host, port, minThread, maxThread, maxIdleTime, idleTimeout, acceptorThreads, selectorThreads, acceptQueueSize, soLingerTime,
                ssl.build());
    }

    public class SslConfigurationBuilder implements ConfigurationBuilder<SslConfiguration> {
        private Optional<String> keyStorePath = absent();
        private Optional<String> keyStorePassword = absent();
        private Optional<String> keyManagerPassword = absent();
        private String keyStoreType = "JKS";
        private Optional<String> trustStorePath = absent();
        private Optional<String> trustStorePassword = absent();
        private String trustStoreType = "JKS";

        public SslConfigurationBuilder keyStore(String path, String password) {
            this.keyStorePath = Optional.of(path);
            this.keyStorePassword = Optional.of(password);
            return this;
        }

        public SslConfigurationBuilder keyManagerPassword(String keyManagerPassword) {
            this.keyManagerPassword = Optional.of(keyManagerPassword);
            return this;
        }

        public SslConfigurationBuilder keyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
            return this;
        }

        public SslConfigurationBuilder trustStore(String path, String password) {
            this.trustStorePath = Optional.of(path);
            this.trustStorePassword = Optional.of(password);
            return this;
        }

        public SslConfigurationBuilder trustStoreType(String trustStoreType) {
            this.trustStoreType = trustStoreType;
            return this;
        }

        public HttpConfigurationBuilder end() {
            return HttpConfigurationBuilder.this;
        }

        public HttpConfiguration.SslConfiguration build() {
            return new HttpConfiguration.SslConfiguration(keyStorePath, keyStorePassword, keyManagerPassword, keyStoreType, trustStorePath, trustStorePassword, trustStoreType);
        }
    }
}