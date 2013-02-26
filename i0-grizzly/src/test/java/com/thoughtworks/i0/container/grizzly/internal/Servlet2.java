package com.thoughtworks.i0.container.grizzly.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
@WebServlet(value = {"/2/1"}, urlPatterns = {"/2/2", "/2/3"})
public class Servlet2 extends HttpServlet {
    private Service service;

    @Inject
    public Servlet2(Service service) {

        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append(service.message());
    }
}
