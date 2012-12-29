package com.thoughtworks.i0.gradle.facets

import com.thoughtworks.i0.gradle.core.Hosting

class Vagrant implements Hosting {
    String name = "vagrant"
    Map<Integer, Integer> ports = [:]
    Map<String, Hosting.Feature> facets = [:]

    def forward(int from, int to) {
        ports[from] = to
    }
}
