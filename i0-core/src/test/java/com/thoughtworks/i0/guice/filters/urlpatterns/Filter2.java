package com.thoughtworks.i0.guice.filters.urlpatterns;

import com.thoughtworks.i0.guice.filters.TestFilter;

import javax.inject.Singleton;
import javax.servlet.annotation.WebFilter;

@Singleton
@WebFilter(urlPatterns = "/s3")
public class Filter2 extends TestFilter {
    public Filter2() {
        super("filter2\n");
    }
}
