package com.thoughtworks.i0.core.internal;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.BindingProvider;
import com.thoughtworks.i0.core.GuiceInjection;
import com.thoughtworks.i0.core.internal.util.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.thoughtworks.i0.core.internal.util.TypePredicates.isModule;

public class GuiceInjectionEnabler implements BindingProvider<GuiceInjection, Configuration> {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationModule.class);

    @Override
    public void configure(Binder binder, GuiceInjection annotation, ApplicationModule<?> module, Configuration configuration) {

        final String[] autoScanPackages = new String[]{module.getClass().getPackage().getName()};

        String[] packages = annotation.packages().length == 0 ? autoScanPackages : annotation.packages();

        if (logger.isInfoEnabled())
            logger.info("Scanning for Guice module classes in packages:\n  {}", on("\n  ").join(packages));

        ClassScanner scanner = new ClassScanner(packages);
        Set<Class<?>> moduleClasses = scanner.findBy(isModule);
        for (Class<?> moduleClass : moduleClasses)
            try {
                binder.install((Module) moduleClass.getConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                logger.warn("Can not instantiate module class '" + moduleClass.getName() + "'", e);
            }

        if (logger.isInfoEnabled())
            logger.info(moduleClasses.isEmpty() ? ("No module classes found") : ("Module classes found:\n  {}"),
                    on("\n  ").join(moduleClasses));
    }
}
