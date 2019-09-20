package edu.utdallas.objectutils.utils;

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

import edu.utdallas.objectutils.AbstractWrappedBasicArray;
import edu.utdallas.objectutils.AbstractWrappedCompositeObject;
import edu.utdallas.objectutils.AbstractWrappedReference;
import edu.utdallas.objectutils.Wrapped;
import edu.utdallas.objectutils.WrappedArray;
import edu.utdallas.objectutils.WrappedBooleanArray;
import edu.utdallas.objectutils.WrappedByteArray;
import edu.utdallas.objectutils.WrappedCharArray;
import edu.utdallas.objectutils.WrappedDoubleArray;
import edu.utdallas.objectutils.WrappedFloatArray;
import edu.utdallas.objectutils.WrappedIntArray;
import edu.utdallas.objectutils.WrappedLongArray;
import edu.utdallas.objectutils.WrappedPrimitiveBooleanArray;
import edu.utdallas.objectutils.WrappedPrimitiveByteArray;
import edu.utdallas.objectutils.WrappedPrimitiveCharArray;
import edu.utdallas.objectutils.WrappedPrimitiveDoubleArray;
import edu.utdallas.objectutils.WrappedPrimitiveFloatArray;
import edu.utdallas.objectutils.WrappedPrimitiveIntArray;
import edu.utdallas.objectutils.WrappedPrimitiveLongArray;
import edu.utdallas.objectutils.WrappedPrimitiveShortArray;
import edu.utdallas.objectutils.WrappedShortArray;
import edu.utdallas.objectutils.WrappedStringArray;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Utility for printing wrapped objects
 * This is used only for debugging purposes
 *
 * @author Ali Ghanbari
 */

public final class ObjectPrinter {
    private static final Stack<String> OBJECT_DESCRIPTIONS = new Stack<>();

    public static String print(final Wrapped wrapped) {
        String objectDescription = "";
        visitedAddresses.clear();
        if (wrapped instanceof AbstractWrappedReference) {
            print0((AbstractWrappedReference) wrapped);
        } else {
            return wrapped.print();
        }
        while (!OBJECT_DESCRIPTIONS.empty()) {
            objectDescription += OBJECT_DESCRIPTIONS.pop();
        }
        return objectDescription;
    }

    private final static Set<Integer> visitedAddresses = new HashSet<>();

    private static String getTypeName(final AbstractWrappedReference wrappedReference) {
        if (wrappedReference instanceof AbstractWrappedCompositeObject) {
            if (wrappedReference instanceof WrappedArray) {
                return "OBJ_ARRAY";
            }
            return ((AbstractWrappedCompositeObject) wrappedReference).getType().getSimpleName();
        } else if (wrappedReference instanceof WrappedBooleanArray) {
            return "java.lang.Boolean[]";
        } else if (wrappedReference instanceof WrappedByteArray) {
            return "java.lang.Byte[]";
        } else if (wrappedReference instanceof WrappedCharArray) {
            return "java.lang.Character[]";
        } else if (wrappedReference instanceof WrappedDoubleArray) {
            return "java.lang.Double[]";
        } else if (wrappedReference instanceof WrappedFloatArray) {
            return "java.lang.Float[]";
        } else if (wrappedReference instanceof WrappedIntArray) {
            return "java.lang.Integer[]";
        } else if (wrappedReference instanceof WrappedLongArray) {
            return "java.lang.Long[]";
        } else if (wrappedReference instanceof WrappedPrimitiveBooleanArray) {
            return "boolean[]";
        } else if (wrappedReference instanceof WrappedPrimitiveByteArray) {
            return "byte[]";
        } else if (wrappedReference instanceof WrappedPrimitiveCharArray) {
            return "char[]";
        } else if (wrappedReference instanceof WrappedPrimitiveDoubleArray) {
            return "double[]";
        } else if (wrappedReference instanceof WrappedPrimitiveFloatArray) {
            return "float[]";
        } else if (wrappedReference instanceof WrappedPrimitiveIntArray) {
            return "int[]";
        } else if (wrappedReference instanceof WrappedPrimitiveLongArray) {
            return "long[]";
        } else if (wrappedReference instanceof WrappedPrimitiveShortArray) {
            return "short[]";
        } else if (wrappedReference instanceof WrappedShortArray) {
            return "java.lang.Short[]";
        } else if (wrappedReference instanceof WrappedStringArray) {
            return "java.lang.String[]";
        }
        throw new IllegalArgumentException();
    }

    private static void print0(final AbstractWrappedReference wrappedReference) {
        visitedAddresses.add(wrappedReference.getAddress());
        final StringBuilder sb = new StringBuilder();
        sb.append(getTypeName(wrappedReference));
        sb.append('@');
        sb.append(wrappedReference.getAddress());
        if (wrappedReference instanceof AbstractWrappedBasicArray) {
            sb.append(wrappedReference.print());
        } else if (wrappedReference instanceof AbstractWrappedCompositeObject) {
            final AbstractWrappedCompositeObject compositeObject =
                    (AbstractWrappedCompositeObject) wrappedReference;
            sb.append('{');
            final Wrapped[] wrappedValues = compositeObject.getValues();
            final int iMax = wrappedValues.length - 1;
            for (int i = 0; iMax >= 0; i++) {
                sb.append(i);
                sb.append(':');
                final Wrapped wrappedValue = wrappedValues[i];
                if (wrappedValue == null) {
                    sb.append("SKIPPED");
                } else if (wrappedValue instanceof AbstractWrappedReference) {
                    final int addr = wrappedValue.getAddress();
                    sb.append('@');
                    sb.append(addr);
                    if (!visitedAddresses.contains(addr)) {
                        visitedAddresses.add(addr);
                        print0((AbstractWrappedReference) wrappedValue);
                    }
                } else {
                    sb.append(wrappedValue.print());
                }
                if (i == iMax) {
                    sb.append('}');
                    break;
                }
                sb.append(',');
            }
            if (iMax < 0) {
                sb.append('}');
            }
        } else {
            throw new IllegalArgumentException();
        }
        OBJECT_DESCRIPTIONS.push(sb.toString());
    }
}
