package com.thoughtworks.i0.core.internal.util;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.sun.jersey.core.spi.scanning.FilesScanner;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sun.jersey.core.reflection.ReflectionHelper.classForName;
import static com.sun.jersey.core.reflection.ReflectionHelper.getContextClassLoader;

public class ClassScanner {
    private final Scanner scanner;

    public ClassScanner(String... packages) {
        this.scanner = new PackageNamesScanner(packages);
    }

    public ClassScanner(CodeSource codeSource) {
        String path = codeSource.getLocation().getPath();
        this.scanner = path.endsWith(".jar") ? new FilesScanner(new File[]{new File(path)}) : new PackageNamesScanner(new String[]{""});
    }

    public Set<Class<?>> findBy(Predicate<Class<?>> predicate) {
        PredicateScannerListener listener = new PredicateScannerListener(predicate);
        scanner.scan(listener);
        return listener.found();
    }

    private class PredicateScannerListener implements ScannerListener, ClassVisitor {
        private final ClassLoader classloader = getContextClassLoader();
        private final Predicate<Class<?>> predicate;
        private final ImmutableSet.Builder<Class<?>> found = new ImmutableSet.Builder<>();

        private PredicateScannerListener(Predicate<Class<?>> predicate) {
            this.predicate = predicate;
        }

        public Set<Class<?>> found() {
            return found.build();
        }

        @Override
        public boolean onAccept(String name) {
            return name.endsWith(".class");
        }

        @Override
        public void onProcess(String name, InputStream in) throws IOException {
            new ClassReader(in).accept(this, 0);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            try {
                Class aClass = checkNotNull(classForName(name.replaceAll("/", "."), classloader));
                if (predicate.apply(aClass)) found.add(aClass);
            } catch (Throwable ignore) {
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
