package edu.utdallas.objectutils;

/*
 * #%L
 * object-utils
 * %%
 * Copyright (C) 2019 The University of Texas at Dallas
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

/**
 * The factory method for wrapped objects.
 * Using the provided static method one can create wrapped objects.
 *
 * @author Ali Ghanbari
 */
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
        Validate.notNull(object);
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
            final Wrapped[] wrappedFieldValues = wrapFieldValuesRecursively(clazz, object);
            return new WrappedObject(clazz, wrappedFieldValues);
        }
    }

    private static Wrapped[] wrapFieldValuesRecursively(final Class<?> clazz, final Object object)
            throws Exception {
        Wrapped[] wrappedFieldValues = new Wrapped[0];
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = FieldUtils.readField(field, object, true);
            final Wrapped wrappedFieldValue = fieldValue == null ? null : wrapObject(fieldValue);
            wrappedFieldValues = Arrays.copyOf(wrappedFieldValues, 1 + wrappedFieldValues.length);
            wrappedFieldValues[wrappedFieldValues.length - 1] = wrappedFieldValue;//objectField;
        }
        return wrappedFieldValues;
    }
}
