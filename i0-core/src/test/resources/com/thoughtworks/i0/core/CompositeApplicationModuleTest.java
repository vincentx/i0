package com.thoughtworks.i0.core;

import org.junit.Test;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class CompositeApplicationModuleTest {
    @Test
    public void test_not_return_null_when_there_is_no_sub_modules() {
        CompositeApplicationModule module = new CompositeApplicationModule();
        assertThat(module.getSubModules(), not(nullValue()));
    }
}
