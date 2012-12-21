package com.thoughtworks.i0.internal.logging;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.thoughtworks.i0.config.LoggingConfiguration;
import com.thoughtworks.i0.config.builder.LoggingConfigurationBuilder;
import com.thoughtworks.i0.config.util.LogLevel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.i0.config.util.LogLevel.*;
import static com.thoughtworks.i0.internal.logging.Logging.configure;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoggingTest {
    private static Logger slf4j = LoggerFactory.getLogger(LoggingTest.class);
    private static java.util.logging.Logger jul = java.util.logging.Logger.getLogger(LoggingTest.class.getName());

    @Test
    public void should_filter_console_log_by_level() {
        test(SLF4J, CONSOLE, DEBUG, "debug", "info", "warn", "error");
        test(SLF4J, FILE, DEBUG, "debug", "info", "warn", "error");
        test(JUL, CONSOLE, DEBUG, "debug fine", "info", "warn", "error");
        test(JUL, FILE, DEBUG, "debug fine", "info", "warn", "error");
    }

    @Test
    public void should_turn_off_all_logs_if_log_level_set_to_off() {
        test(SLF4J, CONSOLE, OFF);
        test(SLF4J, FILE, OFF);
        test(JUL, CONSOLE, OFF);
        test(JUL, FILE, OFF);
    }

    @Test
    public void should_turn_on_all_logs_if_log_level_set_to_all() {
        test(SLF4J, CONSOLE, ALL, "trace", "debug", "info", "warn", "error");
        test(SLF4J, FILE, ALL, "trace", "debug", "info", "warn", "error");
        test(JUL, CONSOLE, ALL, "trace finest", "trace finer", "debug fine", "info", "warn", "error");
        test(JUL, FILE, ALL, "trace finest", "trace finer", "debug fine", "info", "warn", "error");
    }

    @Test
    public void should_turn_on_logs_for_specified_loggers() {
        loggerLevels(SLF4J, CONSOLE, "error");
        loggerLevels(SLF4J, FILE, "error");
        loggerLevels(JUL, CONSOLE, "error");
        loggerLevels(JUL, FILE, "error");
    }

    private void test(LogTest logTest, LogTestContext context, LogLevel level, String... logs) {
        assertThat(copyOf(new LogTestCase(context.config(level), logTest, context).logs()), is(copyOf(logs)));
    }

    private void loggerLevels(LogTest logTest, LogTestContext context, String... logs) {
        assertThat(copyOf(new LogTestCase(context.configLogger(), logTest, context).logs()), is(copyOf(logs)));
    }


    private static class LogTestCase {
        private final LoggingConfiguration config;
        private final LogTest test;
        private final LogTestContext runner;

        private LogTestCase(LoggingConfiguration config, LogTest test, LogTestContext runner) {
            this.config = config;
            this.test = test;
            this.runner = runner;
        }

        public Iterable<String> logs() {

            try {
                runner.before();
                configure(config);
                test.log();

                return runner.logs();
            } finally {
                runner.cleanup();
            }
        }
    }

    private static interface LogTest {
        public void log();
    }

    private static interface LogTestContext {
        void before();

        Iterable<String> logs();

        void cleanup();

        LoggingConfiguration config(LogLevel level);

        LoggingConfiguration configLogger();
    }

    private final static LogTest SLF4J = new LogTest() {
        @Override
        public void log() {
            slf4j.trace("trace");
            slf4j.debug("debug");
            slf4j.info("info");
            slf4j.warn("warn");
            slf4j.error("error");
        }
    };

    private final static LogTest JUL = new LogTest() {
        @Override
        public void log() {
            jul.finest("trace finest");
            jul.finer("trace finer");
            jul.fine("debug fine");
            jul.info("info");
            jul.warning("warn");
            jul.severe("error");
        }
    };

    private final static LogTestContext CONSOLE = new LogTestContext() {
        public final Function<String, String> TRIM = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input.trim();
            }
        };
        public final Predicate<String> NOT_EMPTY = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return !input.isEmpty();
            }
        };
        private PrintStream systemOut;
        private ByteArrayOutputStream out;

        @Override
        public void before() {
            systemOut = System.out;
            out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
        }

        @Override
        public Iterable<String> logs() {
            return filter(transform(on('\n').split(new String(out.toByteArray())), TRIM), NOT_EMPTY);

        }

        @Override
        public void cleanup() {
            System.setOut(systemOut);
        }

        @Override
        public LoggingConfiguration config(LogLevel level) {
            return new LoggingConfigurationBuilder().level(level)
                    .console().format("%m\n")
                    .end().build();
        }

        @Override
        public LoggingConfiguration configLogger() {
            return new LoggingConfigurationBuilder().level(LogLevel.OFF)
                    .logger(LoggingTest.class, LogLevel.ERROR)
                    .console().format("%m\n")
                    .end().build();

        }
    };

    private final static LogTestContext FILE = new LogTestContext() {
        {
            try {
                log = File.createTempFile("i0-logging", ".log");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private File log;

        @Override
        public void before() {
        }

        @Override
        public Iterable<String> logs() {
            try {
                return Files.readLines(log, Charset.defaultCharset());
            } catch (IOException e) {
                return ImmutableList.of();
            }
        }

        @Override
        public void cleanup() {
            log.delete();
        }

        @Override
        public LoggingConfiguration config(LogLevel level) {
            return new LoggingConfigurationBuilder().level(level)
                    .file().filename(log.getAbsolutePath()).format("%m\n")
                    .end().build();

        }

        @Override
        public LoggingConfiguration configLogger() {
            return new LoggingConfigurationBuilder().level(LogLevel.OFF)
                    .logger(LoggingTest.class, LogLevel.ERROR)
                    .file().filename(log.getAbsolutePath()).format("%m\n")
                    .end().build();
        }
    };
}