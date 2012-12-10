package com.thoughtworks.i0.guice;

import com.google.inject.Guice;
import com.thoughtworks.i0.guice.services.Service2;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackageScanningServiceModuleTest {

    @Test
    public void should_set_up_injection_context() {
        PackageScanningServiceModule module = new PackageScanningServiceModule("com.thoughtworks.i0.guice.services");
        Service2 instance = Guice.createInjector(module).getInstance(Service2.class);
        assertThat(instance.message(), is("message"));
    }
}
