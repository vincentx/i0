package com.thoughtworks.i0.config;

import com.google.common.base.Optional;
import com.thoughtworks.i0.config.builder.Builder;
import com.thoughtworks.i0.config.builder.OptionalBuilder;
import com.thoughtworks.i0.config.util.Duration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.thoughtworks.i0.config.util.Duration.Unit.MINUTES;
import static com.thoughtworks.i0.config.util.Duration.Unit.SECONDS;

@XmlType
public class HttpConfiguration {

    public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_MIN_THREAD = 8;
    public static final int DEFAULT_MAX_THREAD = 512;
    public static final Duration DEFAULT_MAX_IDLE_TIME = new Duration(1, MINUTES);
    public static final int DEFAULT_ACCEPTOR_THREADS = 0;
    public static final int DEFAULT_SELECTOR_THREADS = 0;
    public static final int DEFAULT_ACCEPT_QUEUE_SIZE = 0;
    public static final Duration DEFAULT_IDLE_TIMEOUT = new Duration(30, SECONDS);

    @NotNull
    private Optional<String> host = absent();
    @Min(1024)
    @Max(65535)
    private int port = DEFAULT_PORT;
    @Min(1)
    private int minThread = DEFAULT_MIN_THREAD;
    @Min(2)
    private int maxThread = DEFAULT_MAX_THREAD;
    @NotNull
    private Duration maxIdleTime = DEFAULT_MAX_IDLE_TIME;
    @Min(0)
    private int acceptorThreads = DEFAULT_ACCEPTOR_THREADS;
    @Min(0)
    private int selectorThreads = DEFAULT_SELECTOR_THREADS;
    @Min(0)
    private int acceptQueueSize = DEFAULT_ACCEPT_QUEUE_SIZE;
    @NotNull
    private Optional<Duration> soLingerTime = absent();
    @NotNull
    private Duration idleTimeout = DEFAULT_IDLE_TIMEOUT;

    private Optional<SslConfiguration> ssl = Optional.absent();

    @XmlElement
    public Optional<String> getHost() {
        return host;
    }

    @XmlElement
    public int getPort() {
        return port;
    }

    @XmlElement
    public int getMinThread() {
        return minThread;
    }

    @XmlElement
    public int getMaxThread() {
        return maxThread;
    }

    @XmlElement
    public Duration getMaxIdleTime() {
        return maxIdleTime;
    }

    @XmlElement
    public int getAcceptQueueSize() {
        return acceptQueueSize;
    }

    @XmlElement
    public int getAcceptorThreads() {
        return acceptorThreads;
    }

    @XmlElement
    public int getSelectorThreads() {
        return selectorThreads;
    }

    @XmlElement
    public Optional<Duration> getSoLingerTime() {
        return soLingerTime;
    }

    @XmlElement
    public Optional<SslConfiguration> getSsl() {
        return ssl;
    }

    @XmlElement
    public Duration getIdleTimeout() {
        return idleTimeout;
    }

    public HttpConfiguration() {
    }

    private HttpConfiguration(Optional<String> host, int port, int minThread, int maxThread, Duration maxIdleTime, Duration idleTimeout, int acceptorThreads, int selectorThreads, int acceptQueueSize, Optional<Duration> soLingerTime, Optional<SslConfiguration> ssl) {
        this.host = host;
        this.port = port;
        this.minThread = minThread;
        this.maxThread = maxThread;
        this.maxIdleTime = maxIdleTime;
        this.idleTimeout = idleTimeout;
        this.acceptorThreads = acceptorThreads;
        this.selectorThreads = selectorThreads;
        this.acceptQueueSize = acceptQueueSize;
        this.soLingerTime = soLingerTime;
        this.ssl = ssl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpConfiguration that = (HttpConfiguration) o;

        if (acceptQueueSize != that.acceptQueueSize) return false;
        if (acceptorThreads != that.acceptorThreads) return false;
        if (maxThread != that.maxThread) return false;
        if (minThread != that.minThread) return false;
        if (port != that.port) return false;
        if (selectorThreads != that.selectorThreads) return false;
        if (!host.equals(that.host)) return false;
        if (!idleTimeout.equals(that.idleTimeout)) return false;
        if (!maxIdleTime.equals(that.maxIdleTime)) return false;
        if (!soLingerTime.equals(that.soLingerTime)) return false;
        if (!ssl.equals(that.ssl)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + minThread;
        result = 31 * result + maxThread;
        result = 31 * result + maxIdleTime.hashCode();
        result = 31 * result + acceptorThreads;
        result = 31 * result + selectorThreads;
        result = 31 * result + acceptQueueSize;
        result = 31 * result + soLingerTime.hashCode();
        result = 31 * result + idleTimeout.hashCode();
        result = 31 * result + ssl.hashCode();
        return result;
    }

    @XmlType
    public static class SslConfiguration {
        @NotNull
        private Optional<String> keyStorePath = Optional.absent();
        @NotNull
        private Optional<String> keyStorePassword = Optional.absent();
        @NotNull
        private Optional<String> keyManagerPassword = Optional.absent();
        @NotNull
        private String keyStoreType = "JKS";
        @NotNull
        private Optional<String> trustStorePath = Optional.absent();
        @NotNull
        private Optional<String> trustStorePassword = Optional.absent();
        @NotNull
        private String trustStoreType = "JKS";

        private SslConfiguration() {
        }

        public SslConfiguration(Optional<String> keyStorePath, Optional<String> keyStorePassword, Optional<String> keyManagerPassword, String keyStoreType, Optional<String> trustStorePath, Optional<String> trustStorePassword, String trustStoreType) {
            this.keyStorePath = keyStorePath;
            this.keyStorePassword = keyStorePassword;
            this.keyManagerPassword = keyManagerPassword;
            this.keyStoreType = keyStoreType;
            this.trustStorePath = trustStorePath;
            this.trustStorePassword = trustStorePassword;
            this.trustStoreType = trustStoreType;
        }

        @XmlElement
        public Optional<String> getKeyStorePath() {
            return keyStorePath;
        }

        @XmlElement
        public Optional<String> getKeyStorePassword() {
            return keyStorePassword;
        }

        @XmlElement
        public Optional<String> getKeyManagerPassword() {
            return keyManagerPassword;
        }

        @XmlElement
        public String getKeyStoreType() {
            return keyStoreType;
        }

        @XmlElement
        public Optional<String> getTrustStorePath() {
            return trustStorePath;
        }

        @XmlElement
        public Optional<String> getTrustStorePassword() {
            return trustStorePassword;
        }

        @XmlElement
        public String getTrustStoreType() {
            return trustStoreType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SslConfiguration that = (SslConfiguration) o;

            if (!keyManagerPassword.equals(that.keyManagerPassword)) return false;
            if (!keyStorePassword.equals(that.keyStorePassword)) return false;
            if (!keyStorePath.equals(that.keyStorePath)) return false;
            if (!keyStoreType.equals(that.keyStoreType)) return false;
            if (!trustStorePassword.equals(that.trustStorePassword)) return false;
            if (!trustStorePath.equals(that.trustStorePath)) return false;
            if (!trustStoreType.equals(that.trustStoreType)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = keyStorePath.hashCode();
            result = 31 * result + keyStorePassword.hashCode();
            result = 31 * result + keyManagerPassword.hashCode();
            result = 31 * result + keyStoreType.hashCode();
            result = 31 * result + trustStorePath.hashCode();
            result = 31 * result + trustStorePassword.hashCode();
            result = 31 * result + trustStoreType.hashCode();
            return result;
        }
    }

    public static class HttpConfigurationBuilder implements Builder<HttpConfiguration> {
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

        private OptionalBuilder<SslConfigurationBuilder, SslConfiguration> ssl = new OptionalBuilder<>(new SslConfigurationBuilder());
        private Configuration.ConfigurationBuilder parent;

        HttpConfigurationBuilder(Configuration.ConfigurationBuilder parent) {
            this.parent = parent;
        }

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

        public Configuration.ConfigurationBuilder end() {
            return parent;
        }

        public HttpConfiguration build() {
            return new HttpConfiguration(host, port, minThread, maxThread, maxIdleTime, idleTimeout, acceptorThreads, selectorThreads, acceptQueueSize, soLingerTime,
                    ssl.build());
        }

        public class SslConfigurationBuilder implements Builder<SslConfiguration> {
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

            public SslConfiguration build() {
                return new SslConfiguration(keyStorePath, keyStorePassword, keyManagerPassword, keyStoreType, trustStorePath, trustStorePassword, trustStoreType);
            }
        }
    }
}
