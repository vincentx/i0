package com.thoughtworks.i0.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thoughtworks.i0.core.guicemodule.AutoScan;
import com.thoughtworks.i0.core.guicemodule.Specified;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GuiceInjectionFacetTest {

    @Test
    public void should_auto_scan_all_packages() {
        Injector injector = Guice.createInjector(new AutoScan());
        assertThat(injector.getInstance(String.class), is("everything"));
        assertThat(injector.getInstance(Integer.class), is(42));
    }

    @Test(expected = IllegalStateException.class)
    public void should_auto_scan_specified_packages() {
        Injector injector = Guice.createInjector(new Specified());
        assertThat(injector.getInstance(Integer.class), is(42));
        injector.getInstance(String.class);
    }
}
