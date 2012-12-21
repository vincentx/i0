package com.thoughtworks.i0.config;

import com.google.common.base.Optional;
import com.thoughtworks.i0.config.util.Duration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.config.util.Size;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.thoughtworks.i0.config.Configuration.read;
import static com.thoughtworks.i0.config.HttpConfiguration.*;
import static com.thoughtworks.i0.config.builder.ConfigurationBuilder.config;
import static com.thoughtworks.i0.config.builder.DatabaseConfigurationBuilder.H2;
import static com.thoughtworks.i0.config.builder.DatabaseConfigurationBuilder.Hibernate;
import static com.thoughtworks.i0.config.util.Duration.Unit.MILLISECONDS;
import static com.thoughtworks.i0.config.util.Duration.Unit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {

    @Test
    public void should_use_default_value_for_configuration() throws IOException {
        Configuration configuration = read(fixture("use_default_value_for_configuration.yml"));

        HttpConfiguration http = configuration.getHttp();
        assertThat(http.getPort(), is(DEFAULT_PORT));
        assertThat(http.getHost().isPresent(), is(false));
        assertThat(http.getMinThread(), is(DEFAULT_MIN_THREAD));
        assertThat(http.getMaxThread(), is(DEFAULT_MAX_THREAD));
        assertThat(http.getAcceptorThreads(), is(DEFAULT_ACCEPTOR_THREADS));
        assertThat(http.getSelectorThreads(), is(DEFAULT_SELECTOR_THREADS));
        assertThat(http.getAcceptQueueSize(), is(DEFAULT_ACCEPT_QUEUE_SIZE));
        assertThat(http.getMaxIdleTime(), is(DEFAULT_MAX_IDLE_TIME));
        assertThat(http.getIdleTimeout(), is(DEFAULT_IDLE_TIMEOUT));
        assertThat(http.getSoLingerTime().isPresent(), is(false));

        assertThat(http.getSsl(), is(Optional.<SslConfiguration>absent()));

        LoggingConfiguration logging = configuration.getLogging();
        assertThat(logging.getLevel(), is(LogLevel.INFO));
        assertThat(logging.getLoggers().isEmpty(), is(true));
        assertThat(logging.getConsole().isPresent(), is(false));
        assertThat(logging.getFile().isPresent(), is(false));
    }

    @Test
    public void should_populate_configurations() throws IOException {
        Configuration configuration = read(fixture("populate_configurations.yml"));

        assertThat(configuration.getHttp(), is(config().http()
                .port(8081).host("127.0.0.1")
                .threadPool(10, 1024)
                .threads(10, 40)
                .acceptQueueSize(100)
                .maxIdleTime(new Duration(1, SECONDS))
                .idleTimeout(new Duration(500, MILLISECONDS))
                .soLingerTime(new Duration(500, MILLISECONDS))
                .ssl()
                .keyStore("/etc/key.store", "password")
                .keyManagerPassword("managerPassword")
                .keyStoreType("JKS")
                .trustStore("/etc/key.store", "password")
                .trustStoreType("JKS")
                .end()
                .build()));

        assertThat(configuration.getLogging(), is(config().logging()
                .level(LogLevel.DEBUG).logger(ConfigurationTest.class, LogLevel.DEBUG)
                .console()
                .level(LogLevel.DEBUG)
                .format("%h %l %u %t \\\"%r\\\" %s %b")
                .timeZone("GMT")
                .end()
                .file()
                .level(LogLevel.DEBUG)
                .format("%h %l %u %t \\\"%r\\\" %s %b")
                .timeZone("GMT")
                .filename("example.log")
                .archive()
                .maxFileSize(new Size(150, Size.Unit.MB))
                .maxHistory(10)
                .namePattern("example-%d.log.zip")
                .end()
                .end()
                .build()));

        assertThat(configuration.getDatabase().get(), is(config().database().with(H2.driver, H2.compatible("ORACLE"),
                H2.privateMemoryDB, Hibernate.dialect("Oracle")).user("sa").password("")
                .migration()
                .auto(false)
                .locations("db/migration")
                .placeholder("user", "real_user")
                .end()
                .build()));
    }

    private InputStream fixture(String fixture) {
        return getClass().getResourceAsStream(fixture);
    }
}
