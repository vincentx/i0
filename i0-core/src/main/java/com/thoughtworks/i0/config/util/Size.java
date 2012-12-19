package com.thoughtworks.i0.config.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import static com.google.common.base.CharMatcher.*;
import static com.google.common.collect.ImmutableSet.copyOf;

public class Size {
    public static enum Unit {

        KB(1024 * 8, "K", "KILOBYTE", "KILOBYTES"),
        MB(1024 * KB.bits, "M", "MEGABYTE", "MEGABYTES"),
        GB(1024 * MB.bits, "G", "GIGABYTE", "GIGABYTES");

        private final long bits;
        private final ImmutableSet<String> representations;

        private Unit(long bits, String... representations) {
            this.bits = bits;
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

    public Size(double quantity, Unit unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public long value() {
        return (long) (quantity * unit.bits);
    }

    @JsonCreator
    public static Size valueOf(String text) {
        String trimmed = WHITESPACE.removeFrom(text).trim();
        return new Size(quantity(trimmed), unit(trimmed));
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

        Size size = (Size) o;

        if (quantity != size.quantity) return false;
        if (unit != size.unit) return false;

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
    @JsonValue
    public String toString() {
        return quantity + unit.toString();
    }
}
