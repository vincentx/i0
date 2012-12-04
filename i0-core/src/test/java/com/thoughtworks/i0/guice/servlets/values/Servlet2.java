package com.thoughtworks.i0.guice.servlets.values;

import com.thoughtworks.i0.guice.servlets.TestServlet;

import javax.inject.Singleton;
import javax.servlet.annotation.WebServlet;

@Singleton
@WebServlet({"*.jpg"})
public class Servlet2 extends TestServlet {
    public Servlet2() {
        super("servlet2\n");
    }
}
