package com.thoughtworks.i0;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.guice.PackageScanningServiceModule;
import com.thoughtworks.i0.guice.PackageScanningServletModule;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.toArray;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Collections.addAll;

public class ApplicationModule extends AbstractModule {
    public static boolean isApplicationModule(Class<?> module) {
        try {
            return !isAbstract(module.getModifiers()) && module.isAnnotationPresent(Application.class) && isPublic(module.getModifiers())
                    && (module.getConstructor() != null);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static ApplicationModule initialize(Class<?> module) throws Exception {
        return (ApplicationModule) module.getConstructor().newInstance();
    }

    private final Application application;

    private Configuration configuration;

    public ApplicationModule() {
        application = getClass().getAnnotation(Application.class);
    }

    public final void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public final Application getApplication() {
        return application;
    }

    @Override
    protected final void configure() {
        configWeb();
        configApi();
        configService();
        persistUnit();
    }

    protected void configService() {
        scanServices(servicePackages());
    }

    private void scanServices(Set<String> packages) {
        if (packages.size() > 0) install(new PackageScanningServiceModule(toArray(packages, String.class)));
    }

    protected void configApi() {
        scanApi(apiPackages());
    }

    protected void configWeb() {
        scanWeb(webComponentPackages());
    }

    protected final void scanApi(final Set<String> packages) {
        if (packages.size() > 1)
            install(new ServletModule() {
                @Override
                protected void configureServlets() {
                    serve("/api/*").with(GuiceContainer.class, new ImmutableMap.Builder<String, String>()
                            .put(PackagesResourceConfig.PROPERTY_PACKAGES, on(";").skipNulls().join(packages)).build());
                }
            });
    }

    protected final void scanWeb(Set<String> packages) {
        if (packages.size() > 0) install(new PackageScanningServletModule(toArray(packages, String.class)));
    }

    private void persistUnit() {
        if (application.persistUnit() == null || application.persistUnit().isEmpty()) return;
        install(new JpaPersistModule(application.persistUnit()).properties(database()));
        install(new ServletModule() {
            @Override
            protected void configureServlets() {
                filter("/*").through(PersistFilter.class);
            }
        });
    }

    private Properties database() {
        return configuration.getDatabase() == null ? new Properties() : configuration.getDatabase().toProperties();
    }

    protected final Set<String> webComponentPackages() {
        return add(add(add(new HashSet<String>(), application.servlets()), application.filters()), isUsingRoot(), application.root() + ".servlets", application.root() + ".filters");
    }

    protected final Set<String> apiPackages() {
        return add(add(add(new HashSet<String>(), application.api()), "com.fasterxml.jackson.jaxrs.json"), isUsingRoot(), application.root() + ".api");
    }

    protected final Set<String> servicePackages() {
        return add(add(new HashSet<String>(), application.services()), isUsingRoot(), application.root() + ".services");
    }


    protected static Set<String> add(Set<String> set, String... contents) {
        addAll(set, contents);
        return set;
    }

    protected static Set<String> add(Set<String> set, boolean condition, String... contents) {
        if (condition) addAll(set, contents);
        return set;
    }


    protected final boolean isUsingRoot() {
        return application.root() != null && !application.root().isEmpty();
    }
}
