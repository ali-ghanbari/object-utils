package edu.utdallas.objectutils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

interface WrappedObjectPlaceholder {
    void substitute(final Wrapped wrappedObject);
}

class ReifiedObjectPlaceholder {
    /* this is the object whose field is going to be replaced by some reified object */
    final Object sourceObject;

    final Field field;

    public ReifiedObjectPlaceholder(final Object sourceObject, final Field field) {
        this.sourceObject = sourceObject;
        this.field = field;
    }

    public void substitute(final Object reifiedTargetObject) throws Exception {
        FieldUtils.writeField(this.field, this.sourceObject, reifiedTargetObject, true);
    }
}