package edu.utdallas.objectutils;


import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

public final class Wrapper {
    public static WrappedBoolean wrapBoolean(final boolean value) {
        return new WrappedBoolean(value);
    }

    public static WrappedByte wrapByte(final byte value) {
        return new WrappedByte(value);
    }

    public static WrappedChar wrapChar(final char value) {
        return new WrappedChar(value);
    }

    public static WrappedDouble wrapDouble(final double value) {
        return new WrappedDouble(value);
    }

    public static WrappedFloat wrapFloat(final float value) {
        return new WrappedFloat(value);
    }

    public static WrappedInt wrapInt(final int value) {
        return new WrappedInt(value);
    }

    public static WrappedLong wrapLong(final long value) {
        return new WrappedLong(value);
    }

    public static WrappedShort wrapShort(final short value) {
        return new WrappedShort(value);
    }

    public static WrappedString wrapString(final String value) {
        return new WrappedString(value);
    }

    public static Wrapped wrapObject(final Object object) throws Exception {
        final Class<?> clazz = object.getClass();
        if (clazz == Boolean.class) {
            return new WrappedBoolean((Boolean) object);
        } else if (clazz == Byte.class) {
            return new WrappedByte((Byte) object);
        } else if (clazz == Character.class) {
            return new WrappedChar((Character) object);
        } else if (clazz == Short.class) {
            return new WrappedShort((Short) object);
        } else if (clazz == Integer.class) {
            return new WrappedInt((Integer) object);
        } else if (clazz == Float.class) {
            return new WrappedFloat((Float) object);
        } else if (clazz == Double.class) {
            return new WrappedDouble((Double) object);
        } else if (clazz == Long.class) {
            return new WrappedLong((Long) object);
        } else if (clazz == String.class) {
            return new WrappedString((String) object);
        } else { // wrapping a general object
            final ObjectField[] fields = constructFieldsRecursively(clazz, object);
            return new WrappedObject(clazz, fields);
        }
    }

    private static ObjectField[] constructFieldsRecursively(final Class<?> clazz, final Object object)
            throws Exception {
        ObjectField[] objectFields = new ObjectField[0];
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = FieldUtils.readField(field, object, true);
            final Wrapped wrappedFieldValue = wrapObject(fieldValue);
            final String fieldName = field.getName();
            final ObjectField objectField = new ObjectField(fieldName, wrappedFieldValue);
            objectFields = Arrays.copyOf(objectFields, 1 + objectFields.length);
            objectFields[objectFields.length - 1] = objectField;
        }
        return objectFields;
    }
}
