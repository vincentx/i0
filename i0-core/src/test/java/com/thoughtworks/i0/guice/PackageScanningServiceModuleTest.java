package com.thoughtworks.i0.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.thoughtworks.i0.guice.services.Service2;
import com.thoughtworks.i0.guice.servlets.urlpatterns.Servlet1;
import com.thoughtworks.i0.guice.servlets.urlpatterns.Servlet2;
import org.junit.Test;

import javax.servlet.http.HttpServlet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackageScanningServiceModuleTest {
    @Test
    public void should_find_class_by_super_class() {
        PackageNamesScanner scanner = new PackageNamesScanner(new String[]{"com.thoughtworks.i0.guice.servlets.urlpatterns"});
        PackageScanningServiceModule.SuperClassScannerListener listener = new PackageScanningServiceModule.SuperClassScannerListener(HttpServlet.class);
        scanner.scan(listener);
        Set<Class<?>> found = listener.getClasses();
        assertThat(found.size(), is(2));
        assertThat(found.contains(Servlet1.class), is(true));
        assertThat(found.contains(Servlet2.class), is(true));
    }

    @Test
    public void should_set_up_injection_context() {
        PackageScanningServiceModule module = new PackageScanningServiceModule("com.thoughtworks.i0.guice.services");
        Service2 instance = Guice.createInjector(module).getInstance(Service2.class);
        assertThat(instance.message(), is("message"));
    }
}
