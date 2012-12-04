package com.thoughtworks.i0.guice.servlets.values;

import com.thoughtworks.i0.guice.servlets.TestServlet;

import javax.inject.Singleton;
import javax.servlet.annotation.WebServlet;

@Singleton
@WebServlet({"/s1", "/s2"})
public class Servlet1 extends TestServlet {
    public Servlet1() {
        super("servlet1\n");
    }
}
