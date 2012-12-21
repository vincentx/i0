package com.thoughtworks.i0.internal.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.collect.Iterables.filter;

public class ServletAnnotations {
    public static ImmutableSet<String> urlPatterns(Class<?> servletOrFilter) {
        if (servletOrFilter.isAnnotationPresent(WebServlet.class)) {
            WebServlet webServlet = servletOrFilter.getAnnotation(WebServlet.class);
            return copyOf(filter(ImmutableSet.<String>builder().add(webServlet.urlPatterns()).add(webServlet.value()).build(), NOT_EMPTY));
        } else if (servletOrFilter.isAnnotationPresent(WebFilter.class)) {
            WebFilter webFilter = servletOrFilter.getAnnotation(WebFilter.class);
            return copyOf(filter(ImmutableSet.<String>builder().add(webFilter.urlPatterns()).add(webFilter.value()).build(), NOT_EMPTY));
        }
        return of();
    }

    private static final Predicate<String> NOT_EMPTY = new Predicate<String>() {
        @Override
        public boolean apply(@Nullable String input) {
            return !input.isEmpty();
        }
    };

    public static final Function<Class<?>, String> LOG_FORMATTER = new Function<Class<?>, String>() {
        @Nullable
        @Override
        public String apply(@Nullable Class<?> input) {
            return String.format("%s --> %s", input, on(", ").join(urlPatterns(input)));
        }
    };

}
