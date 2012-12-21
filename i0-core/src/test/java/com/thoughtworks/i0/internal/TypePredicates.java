package com.thoughtworks.i0.internal;

import com.google.inject.Module;
import com.thoughtworks.i0.Application;
import com.thoughtworks.i0.ApplicationModule;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

import static com.thoughtworks.i0.internal.util.TypePredicates.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TypePredicates {
    @Test
    public void should_return_true_if_sub_class() {
        assertThat(subClassOf(Module.class).apply(ApplicationModule.class), is(true));
    }

    @Test
    public void should_return_false_if_not_sub_class() {
        assertThat(subClassOf(Module.class).apply(Object.class), is(false));
    }

    @Test
    public void should_return_true_if_annotation_present() {
        assertThat(isPresent(Application.class).apply(AnnotatedClass.class), is(true));
    }

    @Test
    public void should_return_false_if_annotation_not_present() {
        assertThat(isPresent(Test.class).apply(AnnotatedClass.class), is(false));
    }

    @Test
    public void should_return_true_if_class_is_abstract() {
        assertThat(isAbstract.apply(AnnotatedClass.class), is(true));
    }

    @Test
    public void should_return_false_if_class_not_abstract() {
        assertThat(isAbstract.apply(TypePredicates.class), is(false));
    }

    @Test
    public void should_return_true_if_is_http_servlet() {
        assertThat(isHttpServlet.apply(AServlet.class), is(true));
    }

    @Test
    public void should_return_false_if_not_http_servlet() {
        assertThat(isHttpServlet.apply(NotAServlet.class), is(false));
        assertThat(isHttpServlet.apply(ZeroPatternServlet.class), is(false));
        assertThat(isHttpServlet.apply(AbstractServlet.class), is(false));
        assertThat(isHttpServlet.apply(NoAnnotationServlet.class), is(false));
    }

    @Test
    public void should_return_true_if_is_http_filter() {
        assertThat(isFilter.apply(AFilter.class), is(true));
    }

    @Test
    public void should_return_false_if_not_http_filter() {
        assertThat(isFilter.apply(NotAFilter.class), is(false));
        assertThat(isFilter.apply(ZeroPatternFilter.class), is(false));
        assertThat(isFilter.apply(AbstractFilter.class), is(false));
        assertThat(isFilter.apply(NoAnnotationFilter.class), is(false));
    }

    @Application(name = "annotated")
    abstract class AnnotatedClass {
    }

    @WebServlet("/web")
    public class AServlet extends HttpServlet {

    }

    @WebFilter("/filter")
    public class AFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        }

        @Override
        public void destroy() {
        }
    }

    @WebServlet
    public class ZeroPatternServlet extends HttpServlet {

    }

    @WebFilter
    public class ZeroPatternFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        }

        @Override
        public void destroy() {
        }
    }

    public class NotAServlet {

    }

    public class NotAFilter {

    }

    @WebServlet
    public abstract class AbstractServlet extends HttpServlet {

    }

    @WebFilter
    public abstract class AbstractFilter implements Filter {

    }

    public class NoAnnotationServlet extends HttpServlet {

    }

    public class NoAnnotationFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        }

        @Override
        public void destroy() {
        }
    }
}


