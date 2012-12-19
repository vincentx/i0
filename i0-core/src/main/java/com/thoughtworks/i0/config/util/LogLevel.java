package com.thoughtworks.i0.config.util;

import ch.qos.logback.classic.Level;

public enum LogLevel {
    ALL(Level.ALL), TRACE(Level.TRACE), DEBUG(Level.DEBUG), INFO(Level.INFO), WARN(Level.WARN), ERROR(Level.ERROR), OFF(Level.OFF);

    private final Level level;

    private LogLevel(Level level) {
        this.level = level;
    }

    public Level value() {
        return level;
    }
}
