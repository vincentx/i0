package com.thoughtworks.i0;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.annotation.Nullable;
import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.EnumSet;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public abstract class Application {
    private final String name;
    private final ImmutableSet.Builder<Module> modules = new ImmutableSet.Builder<>();

    protected Application(String name) {
        this.name = name;
    }

    protected void module(Module... modules) {
        for (Module module : modules) this.modules.add(module);
    }

    protected void servlet(String... packages) {
        module(new PackageScanningServletModule(packages));
    }

    public static void run(final Application application) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(server, application.name, NO_SESSIONS);
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        handler.addServlet(DefaultServlet.class, "/*");

        handler.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return Guice.createInjector(application.modules.build());
            }
        });
        server.start();
        server.join();
    }

    public static class PackageScanningServletModule extends ServletModule {
        private final Scanner scanner;

        public PackageScanningServletModule(String[] packages) {
            this.scanner = new PackageNamesScanner(packages);
        }

        @Override
        protected void configureServlets() {
            AnnotationScannerListener servlets = new AnnotationScannerListener(WebServlet.class);
            scanner.scan(servlets);
            for (Class<?> servlet : Sets.filter(servlets.getAnnotatedClasses(), IsServlet))
                for (String urlPattern : servlet.getAnnotation(WebServlet.class).urlPatterns())
                    serve(urlPattern).with((Class<? extends HttpServlet>) servlet);
        }

        private static final Predicate<Class<?>> IsServlet = new Predicate<Class<?>>() {
            @Override
            public boolean apply(@Nullable Class<?> input) {
                return HttpServlet.class.isAssignableFrom(input);
            }
        };

    }
}
