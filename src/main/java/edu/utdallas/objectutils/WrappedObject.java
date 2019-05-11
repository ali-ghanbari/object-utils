package edu.utdallas.objectutils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedObject implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final Class<?> clazz;

    private final Wrapped[] wrappedFieldValues;

    public WrappedObject(Class<?> clazz, Wrapped[] wrappedFieldValues) {
        this.clazz = clazz;
        this.wrappedFieldValues = wrappedFieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedObject that = (WrappedObject) o;
        return clazz == that.clazz && Arrays.equals(wrappedFieldValues, that.wrappedFieldValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public Object reconstruct() throws Exception {
        return reconstruct(false);
    }

    @Override
    public Object reconstruct(boolean updateStaticFields) throws Exception {
        final Object rawObject = ObjenesisHelper.newInstance(this.clazz);
        final Iterator<Field> fieldsIterator = FieldUtils.getAllFieldsList(this.clazz).iterator();
        for (final Wrapped wrappedFieldValue : this.wrappedFieldValues) {
            Field field = fieldsIterator.next();
            while (strictlyImmutable(field)) {
                field = fieldsIterator.next();
            }
            if (!Modifier.isStatic(field.getModifiers()) || updateStaticFields) {
                final Object value = wrappedFieldValue == null ? null : wrappedFieldValue.reconstruct();
                FieldUtils.writeField(field, rawObject, value, true);
            }
        }
        return rawObject;
    }

}
