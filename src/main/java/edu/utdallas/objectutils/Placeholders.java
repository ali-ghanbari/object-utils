package edu.utdallas.objectutils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

interface WrappedObjectPlaceholder {
    void substitute(final Wrapped wrappedObject);
}

class ReifiedObjectPlaceholder {
    /* this is the object whose fieldIndex'th field is going to be replaced by some reified object */
    final Object pointerObject;

    /* note: this is 0-based, and is going to count strictly immutable fields also */
    final int fieldIndex;

    ReifiedObjectPlaceholder(final Object pointerObject, int fieldIndex) {
        this.pointerObject = pointerObject;
        this.fieldIndex = fieldIndex;
    }

    public void substitute(final Object reifiedObject) throws Exception {
        final Class clazz = this.pointerObject.getClass();
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        final Field field = fields.get(this.fieldIndex);
        FieldUtils.writeField(field, pointerObject, reifiedObject, true);
    }
}