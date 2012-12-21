package com.thoughtworks.i0.projects.aplication.module.servlets;

import com.thoughtworks.i0.projects.aplication.module.services.CService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
@WebServlet(urlPatterns = "/web/c/1", value = "/web/c/2")
public class CServlet extends HttpServlet {
    @Inject
    private CService service;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append(service.message());
    }
}
