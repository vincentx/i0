package com.thoughtworks.i0.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.scanning.FilesScanner;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import org.objectweb.asm.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.security.CodeSource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassScanner {
    private final Scanner scanner;

    public ClassScanner(String... packages) {
        this.scanner = new PackageNamesScanner(packages);
    }

    public ClassScanner(CodeSource codeSource) {
        String path = codeSource.getLocation().getPath();
        this.scanner = path.endsWith(".jar") ? new FilesScanner(new File[]{new File(path)}) : new PackageNamesScanner(new String[]{""});
    }

    public Set<Class<?>> findBySuperClass(Class<?>... superClasses) {
        SuperClassScannerListener listener = new SuperClassScannerListener(superClasses);
        scanner.scan(listener);
        return listener.getClasses();
    }

    public Set<Class<?>> findByAnnotation(Class<? extends Annotation>... annotationClasses) {
        AnnotationScannerListener listener = new AnnotationScannerListener(annotationClasses);
        scanner.scan(listener);
        return listener.getAnnotatedClasses();
    }

    public static class SuperClassScannerListener implements ScannerListener {

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
                try {
                    final Class aClass = ReflectionHelper.classForName(name.replaceAll("/", "."), classloader);
                    if (aClass == null) return;
                    if (Iterables.any(superClasses, new Predicate<Class<?>>() {
                        @Override
                        public boolean apply(@Nullable Class<?> input) {
                            return input.isAssignableFrom(aClass);
                        }
                    })) classes.add(aClass);
                } catch (NoClassDefFoundError ignore) {
                }
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
