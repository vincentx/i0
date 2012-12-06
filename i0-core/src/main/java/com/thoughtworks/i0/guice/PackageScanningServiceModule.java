package com.thoughtworks.i0.guice;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import org.objectweb.asm.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class PackageScanningServiceModule extends AbstractModule {
    private final Scanner scanner;

    public PackageScanningServiceModule(String... packages) {
        this.scanner = new PackageNamesScanner(packages);
    }

    @Override
    protected void configure() {
        SuperClassScannerListener moduleFinder = new SuperClassScannerListener(Module.class);
        scanner.scan(moduleFinder);
        for (Class<?> moduleClass : moduleFinder.getClasses())
            try {
                install((Module) moduleClass.getConstructor().newInstance());
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException ignored) {
            }
    }

    static class SuperClassScannerListener implements ScannerListener {

        private final ClassLoader classloader = ReflectionHelper.getContextClassLoader();

        private final Set<Class<?>> classes = new LinkedHashSet<>();

        private final Set<Class<?>> superClasses = new LinkedHashSet<>();

        private final SuperClassVisitor classVisitor = new SuperClassVisitor();

        public SuperClassScannerListener(Class<?>... superClasses) {
            Collections.addAll(this.superClasses, superClasses);
        }

        public Set<Class<?>> getClasses() {
            return classes;
        }

        @Override
        public boolean onAccept(String name) {
            return name.endsWith(".class");
        }

        @Override
        public void onProcess(String name, InputStream in) throws IOException {
            new ClassReader(in).accept(classVisitor, 0);
        }

        class SuperClassVisitor implements ClassVisitor {

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                final Class aClass = ReflectionHelper.classForName(name.replaceAll("/", "."), classloader);
                if (aClass == null) return;
                if (Iterables.any(superClasses, new Predicate<Class<?>>() {
                    @Override
                    public boolean apply(@Nullable Class<?> input) {
                        return input.isAssignableFrom(aClass);
                    }
                })) classes.add(aClass);
            }

            @Override
            public void visitSource(String source, String debug) {

            }

            @Override
            public void visitOuterClass(String owner, String name, String desc) {

            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return null;
            }

            @Override
            public void visitAttribute(Attribute attr) {
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return null;
            }

            @Override
            public void visitEnd() {
            }
        }
    }
}
