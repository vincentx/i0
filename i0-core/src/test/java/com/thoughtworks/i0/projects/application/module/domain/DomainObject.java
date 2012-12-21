package com.thoughtworks.i0.projects.application.module.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DomainObject {
    @Id
    @GeneratedValue
    private long id;

    private String text;

    private DomainObject() {
    }

    public DomainObject(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
