package com.thoughtworks.i0.core.internal.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.thoughtworks.i0.config.LoggingConfiguration;
import com.thoughtworks.i0.config.util.LogLevel;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TimeZone;

import static com.thoughtworks.i0.config.LoggingConfiguration.ConsoleConfiguration;
import static com.thoughtworks.i0.config.LoggingConfiguration.FileConfiguration;

public class Logging {

    public static void configure(LoggingConfiguration config) {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        LoggerContext context = root.getLoggerContext();

        context.reset();

        setupLoggerForJUL(context);

        configureLevels(root, config);

        for (ConsoleConfiguration console : config.getConsole().asSet())
            root.addAppender(appender(console, context));

        for (FileConfiguration file : config.getFile().asSet())
            root.addAppender(appender(file, context));

    }

    private static void configureLevels(Logger root, LoggingConfiguration config) {
        root.setLevel(config.getLevel().value());
        for (Map.Entry<String, LogLevel> entry : config.getLoggers().entrySet())
            ((Logger) LoggerFactory.getLogger(entry.getKey())).setLevel(entry.getValue().value());
    }

    private static Appender<ILoggingEvent> appender(ConsoleConfiguration console, LoggerContext context) {
        return configureAppender(new ConsoleAppender<ILoggingEvent>(), console.getLevel(), context, console.getFormat(), console.getTimeZone());
    }

    private static Appender<ILoggingEvent> appender(final FileConfiguration file, final LoggerContext context) {
        return configureAppender(fileAppender(file, context), file.getLevel(), context, file.getFormat(), file.getTimeZone());
    }

    private static FileAppender<ILoggingEvent> fileAppender(FileConfiguration file, LoggerContext context) {
        FileAppender<ILoggingEvent> appender = file.getArchive().transform(rollingFileAppender(context)).or(new FileAppender<ILoggingEvent>());
        appender.setFile(file.getFilename());
        return appender;
    }

    private static Function<FileConfiguration.ArchiveConfiguration, FileAppender<ILoggingEvent>> rollingFileAppender(final LoggerContext context) {
        return new Function<FileConfiguration.ArchiveConfiguration, FileAppender<ILoggingEvent>>() {
            @Nullable
            @Override
            public FileAppender<ILoggingEvent> apply(@Nullable FileConfiguration.ArchiveConfiguration input) {
                RollingFileAppender appender = new RollingFileAppender<ILoggingEvent>();
                SizeAndTimeBasedFNATP<ILoggingEvent> triggeringPolicy = new SizeAndTimeBasedFNATP<>();
                triggeringPolicy.setMaxFileSize(input.getMaxFileSize().toString());
                triggeringPolicy.setContext(context);

                TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
                rollingPolicy.setContext(context);
                rollingPolicy.setFileNamePattern(input.getNamePattern());
                rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);
                triggeringPolicy.setTimeBasedRollingPolicy(rollingPolicy);
                rollingPolicy.setMaxHistory(input.getMaxHistory());

                appender.setRollingPolicy(rollingPolicy);
                appender.setTriggeringPolicy(triggeringPolicy);

                rollingPolicy.setParent(appender);
                rollingPolicy.start();
                return appender;
            }
        };
    }

    private static <T extends OutputStreamAppender<ILoggingEvent>> T configureAppender(T appender, LogLevel level, LoggerContext context, Optional<String> format, TimeZone timeZone) {
        appender.setContext(context);
        appender.setEncoder(encoder(context, format, timeZone));
        appender.addFilter(levelFilter(level));
        appender.start();
        return appender;
    }

    private static ThresholdFilter levelFilter(LogLevel level) {
        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(level.toString());
        filter.start();
        return filter;
    }

    private static PatternLayoutEncoder encoder(LoggerContext context, Optional<String> format, TimeZone timeZone) {
        PatternLayoutEncoder layout = new PatternLayoutEncoder();
        layout.setContext(context);
        layout.setPattern(format
                .or("%-5p [%d{ISO8601," + timeZone.getID() + "}][%thread] %c: %m\n%rEx"));
        layout.start();
        return layout;
    }

    private static void setupLoggerForJUL(LoggerContext context) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        LevelChangePropagator propagator = new LevelChangePropagator();
        propagator.setContext(context);
        propagator.setResetJUL(true);
        propagator.start();
        context.addListener(propagator);
    }
}
