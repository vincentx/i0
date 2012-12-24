package com.thoughtworks.i0.jpa;

import com.google.inject.Injector;
import com.thoughtworks.i0.core.Launcher;
import com.thoughtworks.i0.core.ServletContainer;
import com.thoughtworks.i0.jpa.persist.DomainObject;
import com.thoughtworks.i0.jpa.persist.JpaModule;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class JpaPersistFacetTest {
    private ServletContainer server;

    @Test
    public void should_save_entity_to_database() throws Exception {
        server = Launcher.launch(new JpaModule(), false);

        Injector injector = server.injector();

        EntityManagerFactory factory = injector.getInstance(EntityManagerFactory.class);

        EntityManager entityManager = factory.createEntityManager();

        DomainObject text = new DomainObject("text");
        entityManager.persist(text);

        entityManager.close();

        assertThat(text.getId(), is(not(0L)));
    }

    @Test
    public void should_be_able_to_save_entity_in_servlet() throws Exception {
        server = Launcher.launch(new JpaModule(), false);

        assertThat(get("http://localhost:8080/jpa/persist"), is("domain"));
    }

    @After
    public void after() throws Exception {
        if (server != null) server.stop();
    }

    public static String get(String url) throws Exception {
        HttpClient client = new HttpClient(new SslContextFactory());
        client.start();
        try {
            return new String(client.GET(url).get().getContent());
        } finally {
            client.stop();
        }
    }
}
