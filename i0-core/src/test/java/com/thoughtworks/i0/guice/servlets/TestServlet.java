package com.thoughtworks.i0.guice.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class TestServlet extends HttpServlet {
    private final String message;

    public TestServlet(String message) {
        this.message = message;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append(message).close();
    }
}
