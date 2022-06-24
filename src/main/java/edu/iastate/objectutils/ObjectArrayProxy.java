package edu.iastate.objectutils;

import java.util.List;

class ObjectArrayProxy extends AbstractCompositeObjectProxy {
    private static final long serialVersionUID = 1L;

    public ObjectArrayProxy(final Class<?> type, final List<Proxy> values) {
        super(type, values);
    }
}
