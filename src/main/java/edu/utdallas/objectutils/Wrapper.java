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


import edu.utdallas.objectutils.utils.W;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

/**
 * A set of factory methods for wrapped objects.
 * Using the provided static methods, one can create wrapped objects.
 *
 * @author Ali Ghanbari
 */
public final class Wrapper {
    /* We are using this to wrap cyclic object graphs */
    /* We consult this hash-table to find out if we have already wrapped an object */
    private static final Map<W, Wrapped> wrappedObjects;

    static {
        wrappedObjects = new HashMap<>();
    }

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
        if (object == null) {
            return WrappedNull.INSTANCE;
        }
        wrappedObjects.clear();
        Commons.resetAddressCounter();
        final W hashMapSafeObject = W.of(object);
        return wrap0(hashMapSafeObject);
    }

    private static Wrapped wrap0(final W hashMapSafeObject) throws Exception {
        final Object object = hashMapSafeObject.getCore();
        if (object instanceof Integer) {
            return new WrappedInt((Integer) object);
        } else if (object instanceof String) {
            return new WrappedString((String) object);
        } else if (object instanceof Float) {
            return new WrappedFloat((Float) object);
        } else if (object instanceof Double) {
            return new WrappedDouble((Double) object);
        } else if (object instanceof Long) {
            return new WrappedLong((Long) object);
        } else if (object instanceof Boolean) {
            return new WrappedBoolean((Boolean) object);
        } else if (object instanceof Byte) {
            return new WrappedByte((Byte) object);
        } else if (object instanceof Character) {
            return new WrappedChar((Character) object);
        } else if (object instanceof Short) {
            return new WrappedShort((Short) object);
        }
        final Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            if (object instanceof int[]) {
                return new WrappedPrimitiveIntArray((int[]) object);
            } else if (object instanceof boolean[]) {
                return new WrappedPrimitiveBooleanArray((boolean[]) object);
            } else if (object instanceof byte[]) {
                return new WrappedPrimitiveByteArray((byte[]) object);
            } else if (object instanceof char[]) {
                return new WrappedPrimitiveCharArray((char[]) object);
            } else if (object instanceof short[]) {
                return new WrappedPrimitiveShortArray((short[]) object);
            } else if (object instanceof float[]) {
                return new WrappedPrimitiveFloatArray((float[]) object);
            } else if (object instanceof double[]) {
                return new WrappedPrimitiveDoubleArray((double[]) object);
            } else if (object instanceof long[]) {
                return new WrappedPrimitiveLongArray((long[]) object);
            } else if (object instanceof String[]) {
                return new WrappedStringArray((String[]) object);
            } else if (object instanceof Boolean[]) {
                return new WrappedBooleanArray((Boolean[]) object);
            } else  if (object instanceof Byte[]) {
                return new WrappedByteArray((Byte[]) object);
            } else if (object instanceof Character[]) {
                return new WrappedCharArray((Character[]) object);
            } else if (object instanceof Short[]) {
                return new WrappedShortArray((Short[]) object);
            } else if (object instanceof Integer[]) {
                return new WrappedIntArray((Integer[]) object);
            } else if (object instanceof Float[]) {
                return new WrappedFloatArray((Float[]) object);
            } else if (object instanceof Double[]) {
                return new WrappedDoubleArray((Double[]) object);
            } else if (object instanceof Long[]) {
                return new WrappedLongArray((Long[]) object);
            }
            final Class<?> componentType = clazz.getComponentType();
            final int len = Array.getLength(object);
            final Wrapped[] elements = new Wrapped[len];
            final WrappedObjectArray wrappedObjectArray =
                    new WrappedObjectArray(componentType, elements);
            wrappedObjects.put(hashMapSafeObject, wrappedObjectArray);
            for (int i = 0; i < len; i++) {
                final Object e = Array.get(object, i);
                if (e == null) {
                    elements[i] = WrappedNull.INSTANCE;
                    continue;
                }
                final W hashMapSafeElement = W.of(e);
                final Wrapped target = wrappedObjects.get(hashMapSafeElement);
                if (target != null) { // cycle?
                    elements[i] = target;
                } else {
                    elements[i] = wrap0(hashMapSafeElement);
                }
            }
            return wrappedObjectArray;
        }
        final WrappedObject wrappedObject = new WrappedObject(null, null);
        wrappedObjects.put(hashMapSafeObject, wrappedObject);
        final Wrapped[] wrappedFieldValues = wrapFieldValuesRecursively(wrappedObject, clazz, object);
        wrappedObject.setType(clazz);
        wrappedObject.setValues(wrappedFieldValues);
        return wrappedObject;
    }

    private static Wrapped[] wrapFieldValuesRecursively(final WrappedObject currentWrappedObject,
                                                        final Class<?> clazz,
                                                        final Object object) throws Exception {
        Wrapped[] wrappedFieldValues = new Wrapped[0];
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = FieldUtils.readField(field, object, true);
            final Wrapped wrappedFieldValue;
            if (fieldValue == null) {
                wrappedFieldValue = WrappedNull.INSTANCE;
            } else {
                final W hashMapSafeFieldValue = W.of(fieldValue);
                final Wrapped target = wrappedObjects.get(hashMapSafeFieldValue);
                if (target != null) { // cycle?
                    wrappedFieldValue = target;
                } else {
                    wrappedFieldValue = wrap0(hashMapSafeFieldValue);
                }
            }
            wrappedFieldValues = Arrays.copyOf(wrappedFieldValues, 1 + wrappedFieldValues.length);
            wrappedFieldValues[wrappedFieldValues.length - 1] = wrappedFieldValue;
        }
        return wrappedFieldValues;
    }
}
