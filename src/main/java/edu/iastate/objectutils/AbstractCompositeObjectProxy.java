package edu.iastate.objectutils;

import java.util.List;

abstract class AbstractCompositeObjectProxy implements Proxy {
    String typeName;

    List<Proxy> values;

    protected AbstractCompositeObjectProxy(Class<?> type, List<Proxy> values) {
        this.typeName = type.getTypeName();
        this.values = values;
    }
}
