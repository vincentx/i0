package com.thoughtworks.i0.core;

import com.google.inject.Injector;
import com.google.inject.Module;

public interface ServletContainer {
    void addServletContext(String name, boolean shareNothing, Module... modules);

    Injector injector();

    void start(boolean standalone) throws Exception;

    void stop() throws Exception;
}
