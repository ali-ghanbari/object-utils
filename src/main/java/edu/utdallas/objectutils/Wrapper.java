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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;
import static edu.utdallas.objectutils.Commons.getObjectId;

/**
 * A set of factory methods for wrapped objects.
 * Using the provided static methods, one can create wrapped objects.
 *
 * @author Ali Ghanbari
 */
public final class Wrapper {
    /* we are using this to resolve cyclic pointers between objects */
    /* is to be reset before each wrapping operation */
    private static Map<String, List<WrappedObjectPlaceholder>> todos;

    /* we are using this to avoid re-wrapping already wrapped objects.
    please note that this is an important requirement for correctness of reified objects. */
    /* is to be reset before each wrapping operation */
    private static Map<String, Wrapped> cache;

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
        Commons.resetAddressCounter();
        return _wrapObject(object);
    }

    public synchronized static Wrapped _wrapObject(final Object object) throws Exception {
        Validate.notNull(object);
        todos = new HashMap<>();
        cache = new HashMap<>();
        final String id = getObjectId(object);
        return wrap0(id, object);
    }

    private static Wrapped wrap0(final String objectId, final Object object) throws Exception {
        if (object instanceof Boolean) {
            return new WrappedBoolean((Boolean) object);
        } else if (object instanceof boolean[]) {
            return new WrappedBooleanArray((boolean[]) object);
        } else if (object instanceof Boolean[]) {
            return new WrappedBooleanArray((Boolean[]) object);
        } else if (object instanceof Byte) {
            return new WrappedByte((Byte) object);
        } else if (object instanceof byte[]) {
            return new WrappedByteArray((byte[]) object);
        } else if (object instanceof Byte[]) {
            return new WrappedByteArray((Byte[]) object);
        } else if (object instanceof Character) {
            return new WrappedChar((Character) object);
        } else if (object instanceof char[]) {
            return new WrappedCharArray((char[]) object);
        } else if (object instanceof Character[]) {
            return new WrappedCharArray((Character[]) object);
        } else if (object instanceof Short) {
            return new WrappedShort((Short) object);
        } else if (object instanceof short[]) {
            return new WrappedShortArray((short[]) object);
        } else if (object instanceof Short[]) {
            return new WrappedShortArray((Short[]) object);
        } else if (object instanceof Integer) {
            return new WrappedInt((Integer) object);
        } else if (object instanceof int[]) {
            return new WrappedIntArray((int[]) object);
        } else if (object instanceof Integer[]) {
            return new WrappedIntArray((Integer[]) object);
        } else if (object instanceof Float) {
            return new WrappedFloat((Float) object);
        } else if (object instanceof float[]) {
            return new WrappedFloatArray((float[]) object);
        } else if (object instanceof Float[]) {
            return new WrappedFloatArray((Float[]) object);
        } else if (object instanceof Double) {
            return new WrappedDouble((Double) object);
        } else if (object instanceof double[]) {
            return new WrappedDoubleArray((double[]) object);
        } else if (object instanceof Double[]) {
            return new WrappedDoubleArray((Double[]) object);
        } else if (object instanceof Long) {
            return new WrappedLong((Long) object);
        } else if (object instanceof long[]) {
            return new WrappedLongArray((long[]) object);
        } else if (object instanceof Long[]) {
            return new WrappedLongArray((Long[]) object);
        } else if (object instanceof String) {
            return new WrappedString((String) object);
        } else if (object instanceof String[]) {
            return new WrappedStringArray((String[]) object);
        }
        final Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            final Class<?> componentType = clazz.getComponentType();
            final int len = Array.getLength(object);
            final Wrapped[] elements = new Wrapped[len];
            for (int i = 0; i < len; i++) {
                final Object e = Array.get(object, i);
                elements[i] = e == null ? null : wrapObject(e);
            }
            return new WrappedObjectArray(componentType, elements);
        } else { // wrapping a general object
            final List<WrappedObjectPlaceholder> todoList = new LinkedList<>();
            todos.put(objectId, todoList);
            final WrappedObject wrappedObject = new WrappedObject();
            final Wrapped[] wrappedFieldValues =
                    wrapFieldValuesRecursively(wrappedObject, clazz, object);
            wrappedObject.setClazz(clazz);
            wrappedObject.setWrappedFieldValues(wrappedFieldValues);
            for (final WrappedObjectPlaceholder placeholder : todoList) {
                placeholder.substitute(wrappedObject);
            }
            todos.remove(objectId);
            cache.put(objectId, wrappedObject);
            return wrappedObject;
        }
    }

    private static Wrapped[] wrapFieldValuesRecursively(final WrappedObject currentWrappedObject,
                                                        final Class<?> clazz,
                                                        final Object object) throws Exception {
        Wrapped[] wrappedFieldValues = new Wrapped[0];
        int fieldIndexCounter = 0;
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = FieldUtils.readField(field, object, true);
            final Wrapped wrappedFieldValue;
            if (fieldValue == null) {
                wrappedFieldValue = null;
            } else {
                final String id = getObjectId(fieldValue);
                final List<WrappedObjectPlaceholder> todoList = todos.get(id);
                if (todoList != null) { // cycle?
                    wrappedFieldValue = null;
                    final WrappedObjectPlaceholder todoTask =
                            currentWrappedObject.createPlaceholder(fieldIndexCounter);
                    todoList.add(todoTask);
                } else {
                    /* recompute the wrapped object only once we have not already computed it */
                    final Wrapped wrappedObject = cache.get(id);
                    if (wrappedObject == null) {
                        wrappedFieldValue = wrap0(id, fieldValue);
                    } else {
                        wrappedFieldValue = wrappedObject;
                    }
                }
            }
            wrappedFieldValues = Arrays.copyOf(wrappedFieldValues, 1 + wrappedFieldValues.length);
            wrappedFieldValues[wrappedFieldValues.length - 1] = wrappedFieldValue;
            fieldIndexCounter++;
        }
        return wrappedFieldValues;
    }
}
