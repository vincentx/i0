package com.thoughtworks.i0.server.sample.inject.servlets;

import com.thoughtworks.i0.guice.servlets.TestServlet;
import com.thoughtworks.i0.server.sample.inject.services.MessageProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.annotation.WebServlet;

@Singleton
@WebServlet(urlPatterns = {"/s1", "/s2"})
public class Servlet1 extends TestServlet {

    @Inject
    public Servlet1(MessageProvider provider) {
        super(provider.message());
    }
}
