package edu.iastate.objectutils;

import java.util.List;

class NonDetIterableProxy implements Proxy {
    final List<Proxy> elements;

    public NonDetIterableProxy(final List<Proxy> elements) {
        this.elements = elements;
    }
}
