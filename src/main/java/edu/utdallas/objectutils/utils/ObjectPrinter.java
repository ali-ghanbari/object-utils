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

import edu.utdallas.objectutils.Wrapped;
import edu.utdallas.objectutils.WrappedObject;
import edu.utdallas.objectutils.WrappedObjectArray;

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
        if (wrapped instanceof WrappedObject) {
            printWrappedObject((WrappedObject) wrapped);
        } else if (wrapped instanceof WrappedObjectArray) {
            printWrappedObjectArray((WrappedObjectArray) wrapped);
        } else {
            return wrapped.print();
        }
        while (!OBJECT_DESCRIPTIONS.empty()) {
            objectDescription += OBJECT_DESCRIPTIONS.pop();
        }
        return objectDescription;
    }

    private final static Set<Integer> visitedAddresses = new HashSet<>();

    private static void printWrappedObject(final WrappedObject wrappedObject) {
        visitedAddresses.add(wrappedObject.getAddress());
        final StringBuilder sb = new StringBuilder();
        sb.append(wrappedObject.getType().getName());
        sb.append('@');
        sb.append(wrappedObject.getAddress());
        sb.append('{');
        final Wrapped[] wrappedFieldValues = wrappedObject.getValues();
        final int iMax = wrappedFieldValues.length - 1;
        for (int i = 0; iMax >= 0; i++) {
            sb.append(i);
            sb.append(':');
            final Wrapped fv = wrappedFieldValues[i];
            if (fv instanceof WrappedObject) {
                final int addr = fv.getAddress();
                sb.append('@');
                sb.append(addr);
                if (!visitedAddresses.contains(addr)) {
                    visitedAddresses.add(addr);
                    printWrappedObject((WrappedObject) fv);
                }
            } else if (fv instanceof WrappedObjectArray) {
                final int addr = fv.getAddress();
                sb.append('@');
                sb.append(addr);
                if (!visitedAddresses.contains(addr)) {
                    visitedAddresses.add(addr);
                    printWrappedObjectArray((WrappedObjectArray) fv);
                }
            } else {
                sb.append(fv.print());
            }
            if (i == iMax) {
                sb.append('}');
                break;
            }
            sb.append(',');
        }
        OBJECT_DESCRIPTIONS.push(sb.toString());
    }

    private static void printWrappedObjectArray(final WrappedObjectArray wrappedObjectArray) {
        final Wrapped[] value = wrappedObjectArray.getValues();
        final Class<?> componentType = wrappedObjectArray.getType();
        final int iMax = value.length - 1;
        final StringBuilder sb = new StringBuilder();
        sb.append(componentType.getName());
        sb.append('@');
        sb.append(wrappedObjectArray.getAddress());
        sb.append('[');
        for (int i = 0; iMax >= 0; i++) {
            final Wrapped wrapped = value[i];
            if (wrapped instanceof WrappedObject) {
                final int addr = wrapped.getAddress();
                sb.append('@');
                sb.append(addr);
                if (!visitedAddresses.contains(addr)) {
                    visitedAddresses.add(addr);
                    printWrappedObject((WrappedObject) wrapped);
                }
            } else if (wrapped instanceof WrappedObjectArray) {
                final int addr = wrapped.getAddress();
                sb.append('@');
                sb.append(addr);
                if (!visitedAddresses.contains(addr)) {
                    visitedAddresses.add(addr);
                    printWrappedObjectArray((WrappedObjectArray) wrapped);
                }
            } else {
                sb.append(wrapped.print());
            }
            if (i == iMax) {
                sb.append(']');
                break;
            }
            sb.append(',');
            sb.append(' ');
        }
        OBJECT_DESCRIPTIONS.push(sb.toString());
    }
}
