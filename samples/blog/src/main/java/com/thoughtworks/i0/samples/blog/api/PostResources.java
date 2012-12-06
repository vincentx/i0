package com.thoughtworks.i0.samples.blog.api;

import com.thoughtworks.i0.samples.blog.domain.Post;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/posts")
public class PostResources {
    private EntityManager entityManager;

    @Inject
    public PostResources(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GET
    public List<Post> all() {
        return entityManager.createQuery("select t from Post t", Post.class).getResultList();
    }

}
