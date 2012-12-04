package com.thoughtworks.i0.guice.filters.values;

import com.thoughtworks.i0.guice.filters.TestFilter;

import javax.inject.Singleton;
import javax.servlet.annotation.WebFilter;

@Singleton
@WebFilter("/s1")
public class Filter1 extends TestFilter {
    public Filter1() {
        super("filter1\n");
    }
}
