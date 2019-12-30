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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFields;

/**
 * A set of factory methods for wrapped objects.
 * Using the provided static methods, one can create wrapped objects.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public final class Wrapper {
    private static final Pattern ENUM_PATTERN = Pattern.compile("name|ordinal");

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
        final Class<?> clazz = object.getClass();
        if (clazz == Integer.class) {
            return new WrappedInt((Integer) object);
        } else if (clazz == String.class) {
            return new WrappedString((String) object);
        } else if (clazz == Float.class) {
            return new WrappedFloat((Float) object);
        } else if (clazz == Double.class) {
            return new WrappedDouble((Double) object);
        } else if (clazz == Long.class) {
            return new WrappedLong((Long) object);
        } else if (clazz == Boolean.class) {
            return new WrappedBoolean((Boolean) object);
        } else if (clazz == Byte.class) {
            return new WrappedByte((Byte) object);
        } else if (clazz == Character.class) {
            return new WrappedChar((Character) object);
        } else if (clazz == Short.class) {
            return new WrappedShort((Short) object);
        } else if (clazz == Class.class) {
            return new WrappedClassConstant((Class<?>) object);
        }
        if (clazz.isArray()) {
            WrappedArray wrappedArray = null;
            if (clazz == int[].class) {
                wrappedArray = new WrappedPrimitiveIntArray((int[]) object);
            } else if (clazz == boolean[].class) {
                wrappedArray = new WrappedPrimitiveBooleanArray((boolean[]) object);
            } else if (clazz == byte[].class) {
                wrappedArray = new WrappedPrimitiveByteArray((byte[]) object);
            } else if (clazz == char[].class) {
                wrappedArray = new WrappedPrimitiveCharArray((char[]) object);
            } else if (clazz == short[].class) {
                wrappedArray = new WrappedPrimitiveShortArray((short[]) object);
            } else if (clazz == float[].class) {
                wrappedArray = new WrappedPrimitiveFloatArray((float[]) object);
            } else if (clazz == double[].class) {
                wrappedArray = new WrappedPrimitiveDoubleArray((double[]) object);
            } else if (clazz == long[].class) {
                wrappedArray = new WrappedPrimitiveLongArray((long[]) object);
            } else if (clazz == String[].class) {
                wrappedArray = new WrappedStringArray((String[]) object);
            } else if (clazz == Boolean[].class) {
                wrappedArray = new WrappedBooleanArray((Boolean[]) object);
            } else if (clazz == Byte[].class) {
                wrappedArray = new WrappedByteArray((Byte[]) object);
            } else if (clazz == Character[].class) {
                wrappedArray = new WrappedCharArray((Character[]) object);
            } else if (clazz == Short[].class) {
                wrappedArray = new WrappedShortArray((Short[]) object);
            } else if (clazz == Integer[].class) {
                wrappedArray = new WrappedIntArray((Integer[]) object);
            } else if (clazz == Float[].class) {
                wrappedArray = new WrappedFloatArray((Float[]) object);
            } else if (clazz == Double[].class) {
                wrappedArray = new WrappedDoubleArray((Double[]) object);
            } else if (clazz == Long[].class) {
                wrappedArray = new WrappedLongArray((Long[]) object);
            }
            if (wrappedArray != null) {
                WRAPPED_OBJECTS.put(hashMapSafeObject, wrappedArray);
            } else {
                final Class<?> componentType = clazz.getComponentType();
                final int len = Array.getLength(object);
                final Wrapped[] elements = new Wrapped[len];
                wrappedArray = new WrappedObjectArray(componentType, elements);
                WRAPPED_OBJECTS.put(hashMapSafeObject, wrappedArray);
                for (int i = 0; i < len; i++) {
                    final Object e = Array.get(object, i);
                    if (e == null) {
                        elements[i] = WrappedNull.INSTANCE;
                        continue;
                    }
                    final W hashMapSafeElement = W.of(e);
                    final Wrapped target = WRAPPED_OBJECTS.get(hashMapSafeElement);
                    if (target != null) { // cycle?
                        elements[i] = target;
                    } else {
                        elements[i] = wrap0(hashMapSafeElement, inclusionPredicate);
                    }
                }
            }
            return wrappedArray;
        }
        final WrappedObject wrappedObject;
        if (object instanceof Enum) {
            wrappedObject = new WrappedEnumConstant((Enum<?>) object, null);
        } else {
            wrappedObject = new WrappedObject(clazz, null);
        }
        WRAPPED_OBJECTS.put(hashMapSafeObject, wrappedObject);
        final Wrapped[] wrappedFieldValues = wrapFieldValuesRecursively(clazz, object, inclusionPredicate);
        wrappedObject.setValues(wrappedFieldValues);
        return wrappedObject;
    }


    private static Wrapped[] wrapFieldValuesRecursively(final Class<?> clazz,
                                                        final Object object,
                                                        final InclusionPredicate inclusionPredicate) throws Exception {
        Wrapped[] wrappedFieldValues = EMPTY_WRAPPED_ARRAY;
        for (final Field field : getAllFields(clazz)) {
            // we only care about instance fields, as we are wrapping an *object*
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getDeclaringClass() == Enum.class && ENUM_PATTERN.matcher(field.getName()).matches()) {
                continue;
            }
            final Wrapped wrappedFieldValue;
            if (inclusionPredicate.test(field)) {
                final Object fieldValue = readField(field, object, true);
                if (fieldValue == null) {
                    wrappedFieldValue = WrappedNull.INSTANCE;
                } else {
                    final W hashMapSafeFieldValue = W.of(fieldValue);
                    final Wrapped target = WRAPPED_OBJECTS.get(hashMapSafeFieldValue);
                    if (target != null) { // cycle?
                        wrappedFieldValue = target;
                    } else {
                        wrappedFieldValue = wrap0(hashMapSafeFieldValue, inclusionPredicate);
                    }
                }
            } else {
                // this null value is different from WrappedNull.INSTANCE
                // null values are going to be ignored during unwrapping
                wrappedFieldValue = null;
            }
            wrappedFieldValues = ArrayUtils.add(wrappedFieldValues, wrappedFieldValue);
        }
        return wrappedFieldValues;
    }
}
