package com.thoughtworks.i0.internal.util;

import com.google.common.base.Predicate;
import com.google.inject.Module;
import com.thoughtworks.i0.ApplicationModule;

import javax.annotation.Nullable;
import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.size;
import static java.lang.reflect.Modifier.isAbstract;

public class TypePredicates {

    public static Predicate<Class<?>> subClassOf(final Class<?> aClass) {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(@Nullable Class<?> input) {
                return aClass.isAssignableFrom(input);
            }
        };
    }

    public static Predicate<Class<?>> isPresent(final Class<? extends Annotation> annotation) {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(@Nullable Class<?> input) {
                return input.isAnnotationPresent(annotation);
            }
        };
    }

    public static final Predicate<Class<?>> isAbstract = new Predicate<Class<?>>() {

        @Override
        public boolean apply(@Nullable Class<?> input) {
            return isAbstract(input.getModifiers());
        }
    };

    private static final Predicate<Class<?>> atLeastOneUrlPattern = new Predicate<Class<?>>() {
        @Override
        public boolean apply(@Nullable Class<?> input) {
            return size(ServletAnnotations.urlPatterns(input)) != 0;
        }
    };

    public static final Predicate<Class<?>> defaultConstructor = new Predicate<Class<?>>() {
        @Override
        public boolean apply(@Nullable Class<?> input) {
            return any(copyOf(input.getConstructors()), ZERO_PARAMETER);
        }
    };

    private static final Predicate<Constructor> ZERO_PARAMETER = new Predicate<Constructor>() {
        @Override
        public boolean apply(@Nullable Constructor input) {
            return input.getParameterTypes().length == 0;
        }
    };

    public static final Predicate<Class<?>> isHttpServlet = and(subClassOf(HttpServlet.class),
            not(isAbstract), isPresent(WebServlet.class), atLeastOneUrlPattern);

    public static final Predicate<Class<?>> isFilter = and(subClassOf(Filter.class),
            not(isAbstract), isPresent(WebFilter.class), atLeastOneUrlPattern);

    public static final Predicate<Class<?>> isModule = and(subClassOf(Module.class), not(subClassOf(ApplicationModule.class)),
            not(isAbstract), defaultConstructor);

}