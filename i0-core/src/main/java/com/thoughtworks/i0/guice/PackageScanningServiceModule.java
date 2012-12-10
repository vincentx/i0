package com.thoughtworks.i0.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.thoughtworks.i0.util.ClassScanner;

import java.lang.reflect.InvocationTargetException;

public class PackageScanningServiceModule extends AbstractModule {
    private final ClassScanner scanner;

    public PackageScanningServiceModule(String... packages) {
        this.scanner = new ClassScanner(packages);
    }

    @Override
    protected void configure() {
        for (Class<?> moduleClass : scanner.findBySuperClass(Module.class))
            try {
                install((Module) moduleClass.getConstructor().newInstance());
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException ignored) {
            }
    }
}
