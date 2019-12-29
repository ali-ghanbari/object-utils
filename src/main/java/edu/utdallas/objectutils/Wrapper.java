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
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A set of factory methods for wrapped objects.
 * Using the provided static methods, one can create wrapped objects.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public final class Wrapper {
    /* Let's save one call to expensive new operation */
    private static final Wrapped[] EMPTY_WRAPPED_ARRAY = new Wrapped[0];

    /* We are using this to wrap cyclic object graphs */
    /* We consult this hash-table to find out if we have already wrapped an object */
    private static final Map<W, Wrapped> WRAPPED_OBJECTS;

    static {
        WRAPPED_OBJECTS = new HashMap<>();
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
        return wrapObject(object, InclusionPredicate.INCLUDE_ALL);
    }

    public static Wrapped wrapObject(final Object object,
                                     final InclusionPredicate inclusionPredicate) throws Exception {
        if (object == null) {
            return WrappedNull.INSTANCE;
        }
        WRAPPED_OBJECTS.clear();
        Commons.resetAddressCounter();
        final W hashMapSafeObject = W.of(object);
        return wrap0(hashMapSafeObject, inclusionPredicate);
    }

    private static Wrapped wrap0(final W hashMapSafeObject,
                                 final InclusionPredicate inclusionPredicate) throws Exception {
        final Object object = hashMapSafeObject.getCore();
        Wrapped temp = wrapBasicValue(object);
        if (temp != null) {
            WRAPPED_OBJECTS.put(hashMapSafeObject, temp);
            return temp;
        }
        final CompositeObjectIterator iterator;
        final AbstractWrappedCompositeObject wrappedObject;
        final Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            temp = wrapBasicArray(object);
            if (temp != null) {
                WRAPPED_OBJECTS.put(hashMapSafeObject, temp);
                return temp;
            }
            final Class<?> componentType = clazz.getComponentType();
            wrappedObject = new WrappedObjectArray(componentType, null);
            iterator = CompositeObjectIterator.forArray(object);
        } else {
            if (object instanceof Enum) {
                wrappedObject = new WrappedEnumConstant((Enum<?>) object, null);
            } else {
                wrappedObject = new WrappedObject(clazz, null);
            }
            iterator = CompositeObjectIterator.forObject(object);
        }
        WRAPPED_OBJECTS.put(hashMapSafeObject, wrappedObject);
        final Wrapped[] wrappedValues = wrapValuesRecursively(iterator, inclusionPredicate);
        wrappedObject.setValues(wrappedValues);
        return wrappedObject;
    }

    final static Wrapped[] wrapValuesRecursively(final CompositeObjectIterator iterator,
                                                 final InclusionPredicate inclusionPredicate) throws Exception {
        Wrapped[] wrappedValues = EMPTY_WRAPPED_ARRAY;
        while (iterator.hasNext()) {
            iterator.advanceCursor();
            if (iterator.skippedAtCursor()) {
                continue;
            }
            final Wrapped wrappedValue;
            if (iterator.includedAtCursor(inclusionPredicate)) {
                final Object value = iterator.getAtCursor();
                if (value == null) {
                    wrappedValue = WrappedNull.INSTANCE;
                } else {
                    final W hashMapSafeValue = W.of(value);
                    final Wrapped target = WRAPPED_OBJECTS.get(hashMapSafeValue);
                    if (target != null) { // cycle?
                        wrappedValue = target;
                    } else {
                        wrappedValue = wrap0(hashMapSafeValue, inclusionPredicate);
                    }
                }
            } else {
                // this null value is different from WrappedNull.INSTANCE
                // null values are going to be ignored during unwrapping
                wrappedValue = null;
            }
            wrappedValues = ArrayUtils.add(wrappedValues, wrappedValue);
        }
        return wrappedValues;
    }

    private static Wrapped wrapBasicValue(final Object object) {
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
        } else if (object instanceof Class) {
            return new WrappedClassConstant((Class<?>) object);
        }
        return null;
    }

    private static Wrapped wrapBasicArray(final Object object) {
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
        } else if (object instanceof Byte[]) {
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
        return null;
    }
}
