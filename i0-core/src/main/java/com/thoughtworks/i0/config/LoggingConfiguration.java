package com.thoughtworks.i0.config;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.config.util.Size;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;
import java.util.TimeZone;

@XmlType
public class LoggingConfiguration {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    @XmlType
    public static class ConsoleConfiguration {
        @NotNull
        private LogLevel level = LogLevel.ALL;

        @NotNull
        private Optional<String> format = Optional.absent();

        @NotNull
        private TimeZone timeZone = UTC;

        private ConsoleConfiguration() {
        }

        public ConsoleConfiguration(LogLevel level, Optional<String> format, TimeZone timeZone) {
            this.level = level;
            this.format = format;
            this.timeZone = timeZone;
        }

        @XmlElement
        public LogLevel getLevel() {
            return level;
        }

        @XmlElement
        public Optional<String> getFormat() {
            return format;
        }

        @XmlElement
        public TimeZone getTimeZone() {
            return timeZone;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConsoleConfiguration that = (ConsoleConfiguration) o;

            if (!format.equals(that.format)) return false;
            if (level != that.level) return false;
            if (!timeZone.equals(that.timeZone)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = level.hashCode();
            result = 31 * result + format.hashCode();
            result = 31 * result + timeZone.hashCode();
            return result;
        }
    }

    @XmlType
    public static class FileConfiguration {

        @XmlType
        public static class ArchiveConfiguration {
            @NotNull
            private String namePattern;

            private int maxHistory = 5;

            @NotNull
            private Size maxFileSize = new Size(100, Size.Unit.MB);

            private ArchiveConfiguration() {
            }

            public ArchiveConfiguration(String namePattern, int maxHistory, Size maxFileSize) {
                this.namePattern = namePattern;
                this.maxHistory = maxHistory;
                this.maxFileSize = maxFileSize;
            }

            @XmlElement
            public String getNamePattern() {
                return namePattern;
            }

            @XmlElement
            public int getMaxHistory() {
                return maxHistory;
            }

            @XmlElement
            public Size getMaxFileSize() {
                return maxFileSize;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                ArchiveConfiguration that = (ArchiveConfiguration) o;

                if (maxHistory != that.maxHistory) return false;
                if (!maxFileSize.equals(that.maxFileSize)) return false;
                if (!namePattern.equals(that.namePattern)) return false;

                return true;
            }

            @Override
            public int hashCode() {
                int result = namePattern.hashCode();
                result = 31 * result + maxHistory;
                result = 31 * result + maxFileSize.hashCode();
                return result;
            }
        }

        @NotNull
        private LogLevel level = LogLevel.ALL;

        @NotNull
        private Optional<String> format = Optional.absent();

        @NotNull
        private TimeZone timeZone = UTC;

        @NotNull
        private String filename;


        @NotNull
        private Optional<ArchiveConfiguration> archive = Optional.absent();

        private FileConfiguration() {
        }

        public FileConfiguration(LogLevel level, Optional<String> format, TimeZone timeZone, String filename, Optional<ArchiveConfiguration> archive) {
            this.level = level;
            this.format = format;
            this.timeZone = timeZone;
            this.filename = filename;
            this.archive = archive;
        }

        @XmlElement
        public LogLevel getLevel() {
            return level;
        }

        @XmlElement
        public String getFilename() {
            return filename;
        }

        @XmlElement
        public Optional<String> getFormat() {
            return format;
        }

        @XmlElement
        public Optional<ArchiveConfiguration> getArchive() {
            return archive;
        }

        @XmlElement
        public TimeZone getTimeZone() {
            return timeZone;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FileConfiguration that = (FileConfiguration) o;

            if (!archive.equals(that.archive)) return false;
            if (!filename.equals(that.filename)) return false;
            if (!format.equals(that.format)) return false;
            if (!level.equals(that.level)) return false;
            if (!timeZone.equals(that.timeZone)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = level.hashCode();
            result = 31 * result + filename.hashCode();
            result = 31 * result + format.hashCode();
            result = 31 * result + archive.hashCode();
            result = 31 * result + timeZone.hashCode();
            return result;
        }
    }

    @NotNull
    private LogLevel level = LogLevel.INFO;

    @NotNull
    private Map<String, LogLevel> loggers = ImmutableMap.of();

    @NotNull
    private Optional<ConsoleConfiguration> console = Optional.absent();

    @NotNull
    private Optional<FileConfiguration> file = Optional.absent();

    private LoggingConfiguration() {
    }

    public LoggingConfiguration(LogLevel level, Map<String, LogLevel> loggers, Optional<ConsoleConfiguration> console, Optional<FileConfiguration> file) {
        this.level = level;
        this.loggers = loggers;
        this.console = console;
        this.file = file;
    }

    @XmlElement
    public LogLevel getLevel() {
        return level;
    }

    @XmlElement
    public Map<String, LogLevel> getLoggers() {
        return loggers;
    }

    @XmlElement
    public Optional<ConsoleConfiguration> getConsole() {
        return console;
    }

    @XmlElement
    public Optional<FileConfiguration> getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoggingConfiguration that = (LoggingConfiguration) o;

        if (!console.equals(that.console)) return false;
        if (!file.equals(that.file)) return false;
        if (level != that.level) return false;
        if (!loggers.equals(that.loggers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = level.hashCode();
        result = 31 * result + loggers.hashCode();
        result = 31 * result + console.hashCode();
        result = 31 * result + file.hashCode();
        return result;
    }
}
