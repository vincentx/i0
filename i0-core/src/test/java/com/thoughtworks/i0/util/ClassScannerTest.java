package com.thoughtworks.i0.util;

import com.thoughtworks.i0.guice.servlets.urlpatterns.Servlet1;
import com.thoughtworks.i0.guice.servlets.urlpatterns.Servlet2;
import org.junit.Test;

import javax.inject.Singleton;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClassScannerTest {

    @Test
    public void should_find_classes_by_annotation() {
        ClassScanner scanner = new ClassScanner("com.thoughtworks.i0.guice.servlets.urlpatterns");
        Set<Class<?>> found = scanner.findByAnnotation(WebServlet.class);
        assertThat(found.size(), is(2));
        assertThat(found.contains(Servlet1.class), is(true));
        assertThat(found.contains(Servlet2.class), is(true));
    }

    @Test
    public void should_find_classes_by_super_classes() {
        ClassScanner scanner = new ClassScanner("com.thoughtworks.i0.guice.servlets.urlpatterns");
        Set<Class<?>> found = scanner.findBySuperClass(HttpServlet.class);
        assertThat(found.size(), is(2));
        assertThat(found.contains(Servlet1.class), is(true));
        assertThat(found.contains(Servlet2.class), is(true));
    }
}
