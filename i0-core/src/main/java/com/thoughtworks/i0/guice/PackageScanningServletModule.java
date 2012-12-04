package com.thoughtworks.i0.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.Map;

import static com.google.common.collect.Iterables.toArray;

public class PackageScanningServletModule extends ServletModule {
    private final Scanner scanner;

    public PackageScanningServletModule(String... packages) {
        this.scanner = new PackageNamesScanner(packages);
    }

    @Override
    protected void configureServlets() {
        AnnotationScannerListener components = new AnnotationScannerListener(WebServlet.class, WebFilter.class);
        scanner.scan(components);
        for (Class<?> component : components.getAnnotatedClasses())
            if (HttpServlet.class.isAssignableFrom(component))
                bind(component.getAnnotation(WebServlet.class), component);
            else if (Filter.class.isAssignableFrom(component))
                bind(component.getAnnotation(WebFilter.class), component);
    }

    private void bind(WebServlet servlet, Class<?> servletClass) {
        ImmutableList<String> urlPatterns = urlPatterns(servlet.value(), servlet.urlPatterns());
        serve(urlPatterns.get(0), tail(urlPatterns)).with((Class<? extends HttpServlet>) servletClass, initParams(servlet.initParams()));
    }

    private void bind(WebFilter filter, Class<?> filterClass) {
        ImmutableList<String> urlPatterns = urlPatterns(filter.value(), filter.urlPatterns());
        filter(urlPatterns.get(0), tail(urlPatterns)).through((Class<? extends Filter>) filterClass, initParams(filter.initParams()));
    }

    private ImmutableList<String> urlPatterns(String[] value, String[] elements) {
        return new ImmutableSet.Builder<String>().add(value).add(elements).build().asList();
    }

    private Map<String, String> initParams(WebInitParam[] initParams) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        for (WebInitParam initParam : initParams)
            builder.put(initParam.name(), initParam.value());
        return builder.build();
    }

    private String[] tail(ImmutableList<String> urlPatterns) {
        return urlPatterns.size() > 1 ? toArray(urlPatterns.subList(1, urlPatterns.size()), String.class) : new String[0];
    }
}
