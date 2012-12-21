package com.thoughtworks.i0.config.builder;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.i0.config.LoggingConfiguration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.config.util.Size;

import java.util.TimeZone;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class LoggingConfigurationBuilder implements Builder<LoggingConfiguration> {
    private ConfigurationBuilder parent;

    public class ConsoleConfigurationBuilder implements Builder<LoggingConfiguration.ConsoleConfiguration> {
        private LogLevel level = LogLevel.ALL;
        private Optional<String> format = absent();
        private TimeZone timeZone = TimeZone.getTimeZone("UTC");

        public ConsoleConfigurationBuilder() {
        }

        public ConsoleConfigurationBuilder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public ConsoleConfigurationBuilder format(String format) {
            this.format = of(format);
            return this;
        }

        public ConsoleConfigurationBuilder timeZone(String timeZone) {
            this.timeZone = TimeZone.getTimeZone(timeZone);
            return this;
        }

        public LoggingConfiguration.ConsoleConfiguration build() {
            return new LoggingConfiguration.ConsoleConfiguration(level, format, timeZone);
        }

        public LoggingConfigurationBuilder end() {
            return LoggingConfigurationBuilder.this;
        }
    }

    public class FileConfigurationBuilder implements Builder<LoggingConfiguration.FileConfiguration> {
        private LogLevel level = LogLevel.ALL;
        private Optional<String> format = Optional.absent();
        private TimeZone timeZone = TimeZone.getTimeZone("UTC");
        private String filename;

        private OptionalBuilder<ArchiveConfigurationBuilder, LoggingConfiguration.FileConfiguration.ArchiveConfiguration>
                archive = new OptionalBuilder<>(new ArchiveConfigurationBuilder());

        public FileConfigurationBuilder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public FileConfigurationBuilder format(String format) {
            this.format = of(format);
            return this;
        }

        public FileConfigurationBuilder timeZone(String timeZone) {
            this.timeZone = TimeZone.getTimeZone(timeZone);
            return this;
        }

        public FileConfigurationBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public ArchiveConfigurationBuilder archive() {
            return this.archive.builder();
        }

        public LoggingConfiguration.FileConfiguration build() {
            return new LoggingConfiguration.FileConfiguration(level, format, timeZone, filename, archive.build());
        }

        public LoggingConfigurationBuilder end() {
            return LoggingConfigurationBuilder.this;
        }

        public class ArchiveConfigurationBuilder implements Builder<LoggingConfiguration.FileConfiguration.ArchiveConfiguration> {
            private String namePattern;
            private int maxHistory = 5;
            private Size maxFileSize = new Size(100, Size.Unit.MB);

            public ArchiveConfigurationBuilder namePattern(String namePattern) {
                this.namePattern = namePattern;
                return this;
            }

            public ArchiveConfigurationBuilder maxHistory(int maxHistory) {
                this.maxHistory = maxHistory;
                return this;
            }

            public ArchiveConfigurationBuilder maxFileSize(Size maxFileSize) {
                this.maxFileSize = maxFileSize;
                return this;
            }

            public LoggingConfiguration.FileConfiguration.ArchiveConfiguration build() {
                return new LoggingConfiguration.FileConfiguration.ArchiveConfiguration(namePattern, maxHistory, maxFileSize);
            }

            public FileConfigurationBuilder end() {
                return FileConfigurationBuilder.this;
            }
        }
    }

    private LogLevel level = LogLevel.INFO;
    private ImmutableMap.Builder<String, LogLevel> loggers = new ImmutableMap.Builder<>();

    private OptionalBuilder<ConsoleConfigurationBuilder, LoggingConfiguration.ConsoleConfiguration> console =
            new OptionalBuilder<>(new ConsoleConfigurationBuilder());
    private OptionalBuilder<FileConfigurationBuilder, LoggingConfiguration.FileConfiguration> file =
            new OptionalBuilder<>(new FileConfigurationBuilder());

    public LoggingConfigurationBuilder(ConfigurationBuilder parent) {
        this.parent = parent;
    }

    public LoggingConfigurationBuilder level(LogLevel level) {
        this.level = level;
        return this;
    }

    public LoggingConfigurationBuilder logger(String className, LogLevel level) {
        loggers.put(className, level);
        return this;
    }

    public LoggingConfigurationBuilder logger(Class<?> aClass, LogLevel level) {
        loggers.put(aClass.getName(), level);
        return this;
    }


    public ConsoleConfigurationBuilder console() {
        return console.builder();
    }

    public FileConfigurationBuilder file() {
        return file.builder();
    }

    public ConfigurationBuilder end() {
        return parent;
    }

    public LoggingConfiguration build() {
        return new LoggingConfiguration(level, loggers.build(), console.build(), file.build());
    }
}