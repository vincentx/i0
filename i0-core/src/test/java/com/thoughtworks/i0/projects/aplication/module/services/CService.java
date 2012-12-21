package com.thoughtworks.i0.projects.aplication.module.services;

public interface CService {
    public String message();
}

class CServiceImpl implements CService {

    @Override
    public String message() {
        return "/web/c";
    }
}
