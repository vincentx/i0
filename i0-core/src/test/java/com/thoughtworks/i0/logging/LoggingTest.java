package com.thoughtworks.i0.logging;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.thoughtworks.i0.config.LoggingConfiguration;
import com.thoughtworks.i0.config.builder.LoggingConfigurationBuilder;
import com.thoughtworks.i0.config.util.LogLevel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.i0.logging.Logging.configure;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoggingTest {
    private Logger slf4j = LoggerFactory.getLogger(LoggingTest.class);
    private java.util.logging.Logger jul = java.util.logging.Logger.getLogger(LoggingTest.class.getName());

    @Test
    public void slf4j_logger_should_filter_console_log_by_level() {
        ConsoleTestCase test = slf4jLogging(new LoggingConfigurationBuilder().level(LogLevel.INFO)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()), is(copyOf(new String[]{"info", "warn", "error"})));
    }

    @Test
    public void slf4j_logger_should_turn_off_all_logs_if_log_level_set_to_off() {
        ConsoleTestCase test = slf4jLogging(new LoggingConfigurationBuilder().level(LogLevel.OFF)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()).isEmpty(), is(true));
    }

    @Test
    public void slf4j_logger_should_turn_on_all_logs_if_log_level_set_to_all() {
        ConsoleTestCase test = slf4jLogging(new LoggingConfigurationBuilder().level(LogLevel.ALL)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()), is(copyOf(new String[]{"trace", "debug", "info", "warn", "error"})));
    }

    @Test
    public void slf4j_logger_should_turn_on_logs_for_specified_loggers() {
        ConsoleTestCase test = slf4jLogging(new LoggingConfigurationBuilder().level(LogLevel.OFF)
                .logger(LoggingTest.class, LogLevel.ERROR)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()), is(copyOf(new String[]{"error"})));
    }

    @Test
    public void jul_logger_should_filter_console_log_by_level() {
        ConsoleTestCase test = julLogging(new LoggingConfigurationBuilder().level(LogLevel.DEBUG)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()), is(copyOf(new String[]{"debug fine", "info", "warn", "error"})));
    }

    @Test
    public void jul_logger_should_turn_off_all_logs_if_log_level_set_to_off() {
        ConsoleTestCase test = julLogging(new LoggingConfigurationBuilder().level(LogLevel.OFF)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()).isEmpty(), is(true));
    }

    @Test
    public void jul_logger_should_turn_on_all_logs_if_log_level_set_to_all() {
        ConsoleTestCase test = julLogging(new LoggingConfigurationBuilder().level(LogLevel.ALL)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()), is(copyOf(new String[]{"trace finest", "trace finer", "debug fine", "info", "warn", "error"})));
    }

    @Test
    public void jul_logger_should_turn_on_logs_for_specified_loggers() {
        ConsoleTestCase test = julLogging(new LoggingConfigurationBuilder().level(LogLevel.OFF)
                .logger(LoggingTest.class, LogLevel.ERROR)
                .console().format("%m\n")
                .end().build());

        assertThat(copyOf(test.logs()), is(copyOf(new String[]{"error"})));
    }


    private ConsoleTestCase julLogging(final LoggingConfiguration config) {
        return new ConsoleTestCase(config) {

            @Override
            protected void log() {
                jul.finest("trace finest");
                jul.finer("trace finer");
                jul.fine("debug fine");
                jul.info("info");
                jul.warning("warn");
                jul.severe("error");
            }
        };
    }

    private ConsoleTestCase slf4jLogging(final LoggingConfiguration config) {
        return new ConsoleTestCase(config) {

            @Override
            protected void log() {
                slf4j.trace("trace");
                slf4j.debug("debug");
                slf4j.info("info");
                slf4j.warn("warn");
                slf4j.error("error");
            }
        };
    }


    private static abstract class ConsoleTestCase {
        public static final Function<String, String> TRIM = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input.trim();
            }
        };
        public static final Predicate<String> NOT_EMPTY = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return !input.isEmpty();
            }
        };
        private final LoggingConfiguration config;

        private ConsoleTestCase(LoggingConfiguration config) {
            this.config = config;
        }

        protected abstract void log();

        public Iterable<String> logs() {
            PrintStream systemOut = System.out;
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                System.setOut(new PrintStream(out));

                configure(config);
                log();

                return filter(transform(on('\n').split(new String(out.toByteArray())), TRIM), NOT_EMPTY);
            } finally {
                System.setOut(systemOut);
            }
        }
    }
}
