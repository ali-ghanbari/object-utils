package edu.iastate.objectutils;

import java.util.List;

class ObjectProxy extends AbstractCompositeObjectProxy {
    private static final long serialVersionUID = 1L;

    ObjectProxy(final Class<?> type, final List<Proxy> values) {
        super(type, values);
    }
}
