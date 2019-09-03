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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility functions common to all modules
 *
 * @author Ali Ghanbari
 */
public final class Commons {
    public static boolean strictlyImmutable(final Field field) {
        final int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    private static int addressCounter = 0;

    public static int newAddress() {
        return addressCounter++;
    }

    public static void resetAddressCounter() {
        addressCounter = 0;
    }


    //-------------------------------------------- copied from Apache Commons Lang
    //---------------------------------------------due to dependency incompatibility
    //---------------------------------------------with some of Defects4J programs

    private static boolean setAccessibleWorkaround(final AccessibleObject o) {
        if (o == null || o.isAccessible()) {
            return false;
        }
        final Member m = (Member) o;
        if (!o.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                o.setAccessible(true);
                return true;
            } catch (final SecurityException e) { // NOPMD
                // ignore in favor of subsequent IllegalAccessException
            }
        }
        return false;
    }

    private static final int ACCESS_TEST = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

    private static boolean isPackageAccess(final int modifiers) {
        return (modifiers & ACCESS_TEST) == 0;
    }

    public static Object readField(final Field field, final Object target, final boolean forceAccess) throws IllegalAccessException {
        isTrue(field != null, "The field must not be null");
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            setAccessibleWorkaround(field);
        }
        return field.get(target);
    }

    private static void isTrue(final boolean expression, final String message, final Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static List<Field> getAllFieldsList(final Class<?> cls) {
        isTrue(cls != null, "The class must not be null");
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    public static void writeField(final Field field, final Object target, final Object value, final boolean forceAccess)
            throws IllegalAccessException {
        isTrue(field != null, "The field must not be null");
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            setAccessibleWorkaround(field);
        }
        field.set(target, value);
    }
}
