package com.thoughtworks.i0.config.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.CharMatcher.*;
import static com.google.common.collect.ImmutableSet.copyOf;

public class Duration {
    public static enum Unit {
        MILLISECONDS(TimeUnit.MILLISECONDS, "MS", "MILLISECOND"),
        SECONDS(TimeUnit.SECONDS, "S", "SECOND"),
        MINUTES(TimeUnit.MINUTES, "M", "MINUTE"),
        HOURS(TimeUnit.HOURS, "H", "HOUR"),
        DAYS(TimeUnit.DAYS, "D", "DAY");

        private final TimeUnit timeUnit;
        private final ImmutableSet<String> representations;

        private Unit(TimeUnit timeUnit, String... representations) {
            this.timeUnit = timeUnit;
            this.representations = copyOf(representations);
        }

        private static Optional<Unit> parse(final String name) {
            return Iterables.tryFind(copyOf(Unit.values()), new Predicate<Unit>() {
                @Override
                public boolean apply(@Nullable Unit input) {
                    return input.toString().equals(name) || input.representations.contains(name);
                }
            });
        }
    }

    private final double quantity;
    @NotNull
    private final Unit unit;

    public Duration(double quantity, Unit unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public long value() {
        return (long) (quantity * unit.timeUnit.toMillis(1));
    }

    @JsonCreator
    public static Duration valueOf(String text) {
        String trimmed = WHITESPACE.removeFrom(text).trim();
        return new Duration(quantity(trimmed), unit(trimmed));
    }

    private static double quantity(String trimmed) {
        try {
            return Double.parseDouble(JAVA_LETTER.trimTrailingFrom(trimmed));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid size format");
        }
    }

    private static Unit unit(String trimmed) {
        Optional<Unit> unit = Unit.parse(DIGIT.trimLeadingFrom(trimmed).toUpperCase());
        Preconditions.checkArgument(unit.isPresent(), "invalid size format");
        return unit.get();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration duration = (Duration) o;

        if (Double.compare(duration.quantity, quantity) != 0) return false;
        if (unit != duration.unit) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = quantity != +0.0d ? Double.doubleToLongBits(quantity) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + unit.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Duration{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                '}';
    }
}
