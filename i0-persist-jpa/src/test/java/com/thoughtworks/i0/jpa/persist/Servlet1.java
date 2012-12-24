package com.thoughtworks.i0.jpa.persist;

import javax.inject.Inject;
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
@WebServlet("/persist")
public class Servlet1 extends HttpServlet {

    private final EntityManagerFactory factory;
    private final long savedId;

    @Inject
    public Servlet1(EntityManagerFactory factory) {
        this.factory = factory;
        this.savedId = popluateData(factory).getId();
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
        EntityManager entityManager = factory.createEntityManager();
        resp.getWriter().append(entityManager.find(DomainObject.class, savedId).getText());
    }
}
