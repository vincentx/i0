package com.thoughtworks.i0.projects.application.module.servlets;

import com.google.inject.Inject;
import com.thoughtworks.i0.projects.application.module.domain.DomainObject;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
@WebServlet("/persist/a")
public class PersistenceServlet extends HttpServlet {
    private final long savedId;
    private EntityManagerFactory managerFactory;

    @Inject
    public PersistenceServlet(EntityManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
        this.savedId = popluateData(managerFactory).getId();

    }

    private DomainObject popluateData(EntityManagerFactory managerFactory) {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        DomainObject domain = new DomainObject("domain");
        entityManager.persist(domain);
        transaction.commit();
        entityManager.close();
        return domain;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager entityManager = managerFactory.createEntityManager();
        resp.getWriter().append(entityManager.find(DomainObject.class, savedId).getText());
    }
}
