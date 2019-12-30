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
import org.apache.commons.lang3.mutable.MutableLong;

import static org.apache.commons.lang3.reflect.FieldUtils.getAllFields;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic object utilities such as computing deep hash code and
 * shallow copying of arbitrary objects
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public final class ObjectUtils {
    private static final Map<W, MutableLong> VISITED = new HashMap<>();

    private ObjectUtils() {

    }

    /**
     * Note that <code>deepHashCode(object)</code> is not necessarily equal to <code>object.hashCode()</code>,
     * nor do we guarantee <code>deepHashCode(o1) == deepHashCode(o2)</code> implies <code>o1.equals(o2)</code>.
     * However, it is guaranteed that <code> o1 == o2</code> iff <code>deepHashCode(o1) == deepHashCode(o2)</code>.
     * Furthermore, we expect for "most of the cases" we will have
     * <code>deepHashCode(o1) == deepHashCode(o2)</code> iff <code>o1.equals(o2)</code>.
     *
     * Note that you might get different hash codes for different JVM sessions if the structure of <code>object</code>
     * depends on the JVM session. For example, a <code>HashMap</code> object within which you have added class
     * constants <code>String.class</code>, <code>Integer.class</code>, and so on.
     *
     * @param object the object
     * @return deep hash code computed for <code>object</code>
     * @throws Exception any reflection related exception
     */
    public static long deepHashCode(final Object object) throws Exception {
        return deepHashCode(object, InclusionPredicate.INCLUDE_ALL);
    }

    /**
     * Computes deep hash code while ignoring some fields
     * @param object the object for which the hash code should be calculated
     * @param inclusionPredicate the predicate that indicates which field should be included
     * @return deep hash code for <code>object</code>
     * @throws Exception any reflection related exception
     */
    public static long deepHashCode(final Object object,
                                    final InclusionPredicate inclusionPredicate) throws Exception {
        VISITED.clear();
        return deepHashCode(W.of(object), inclusionPredicate, VISITED);
    }

    private static long deepHashCode(final W hashMapSafeObject,
                                     final InclusionPredicate inclusionPredicate,
                                     final Map<W, MutableLong> visited) throws Exception {
        final Object core = hashMapSafeObject.getCore();
        if (core == null) {
            return 0L;
        }
        final Class<?> clazz = core.getClass();
        if (clazz == Boolean.class
                || clazz == Character.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == Void.class
                || clazz == String.class) {
            return core.hashCode();
        } else if (core instanceof Class) {
            return ((Class<?>) core).getName().hashCode();
        } else if (clazz.isEnum()) {
            return ((Enum<?>) core).name().hashCode();
        }
        // composite object
        MutableLong result = visited.get(hashMapSafeObject);
        if (result != null) { // return the already computed hash code, if there is any
            return result.longValue();
        }
        result = new MutableLong(0L);
        visited.put(hashMapSafeObject, result);
        long inner = 0L;
        if (clazz.isArray()) {
            if (clazz == byte[].class) {
                inner = Arrays.hashCode((byte[]) core);
            } else if (clazz == char[].class) {
                inner = Arrays.hashCode((char[]) core);
            } else if (clazz == short[].class) {
                inner = Arrays.hashCode((short[]) core);
            } else if (clazz == int[].class) {
                inner = Arrays.hashCode((int[]) core);
            } else if (clazz == boolean[].class) {
                inner = Arrays.hashCode((boolean[]) core);
            } else if (clazz == float[].class) {
                inner = Arrays.hashCode((float[]) core);
            } else if (clazz == long[].class) {
                inner = Arrays.hashCode((long[]) core);
            } else if (clazz == double[].class) {
                inner = Arrays.hashCode((double[]) core);
            } else if (clazz == String[].class) {
                inner = Arrays.hashCode((String[]) core);
            } else if (clazz == Byte[].class) {
                inner = Arrays.hashCode((Byte[]) core);
            } else if (clazz == Character[].class) {
                inner = Arrays.hashCode((Character[]) core);
            } else if (clazz == Short[].class) {
                inner = Arrays.hashCode((Short[]) core);
            } else if (clazz == Integer[].class) {
                inner = Arrays.hashCode((Integer[]) core);
            } else if (clazz == Boolean[].class) {
                inner = Arrays.hashCode((Boolean[]) core);
            } else if (clazz == Float[].class) {
                inner = Arrays.hashCode((Float[]) core);
            } else if (clazz == Long[].class) {
                inner = Arrays.hashCode((Long[]) core);
            } else if (clazz == Double[].class) {
                inner = Arrays.hashCode((Double[]) core);
            } else {
                final int len = Array.getLength(core);
                for (int i = 0; i < len; i++) {
                    final W wElement = W.of(Array.get(core, i));
                    inner = inner * 31L + deepHashCode(wElement, inclusionPredicate, visited);
                }
            }
        } else {
            for (final Field field : getAllFields(clazz)) {
                if (Modifier.isStatic(field.getModifiers()) || !inclusionPredicate.test(field)) {
                    continue;
                }
                final W wFieldValue = W.of(readField(field, core, true));
                inner = inner * 31L + deepHashCode(wFieldValue, inclusionPredicate, visited);
            }
        }
        result.setValue(clazz.getName().hashCode() * 31L + inner);
        return result.longValue();
    }

	public static <T> void shallowCopy(final T dest, final T src) throws Exception {
		final Class<?> clazz = src.getClass();
		final List<Field> fields = getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            final Object fieldValue = readField(field, src, true);
            writeField(field, dest, fieldValue, true);
        }
	}
}
