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

import static edu.utdallas.objectutils.Commons.strictlyImmutable;
import static edu.utdallas.objectutils.Commons.getAllFieldsList;
import static edu.utdallas.objectutils.Commons.readField;
import static edu.utdallas.objectutils.Commons.writeField;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic object utilities such as computing deep hash code and
 * shallow copying of arbitrary objects
 *
 * @author Ali Ghanbari
 */
public final class ObjectUtils {
    private static final Set<Class<?>> WRAPPER_TYPES;

    private static final Map<W, MutableLong> VISITED;

    static {
        WRAPPER_TYPES = getWrapperTypes();
        VISITED = new HashMap<>();
    }

    private ObjectUtils() {

    }

    /**
     * Note that <code>deepHashCode(object)</code> is not necessarily equal to <code>object.hashCode()</code>,
     * nor do we guarantee <code>deepHashCode(o1) == deepHashCode(o2)</code> implies <code>o1.equals(o2)</code>.
     * However, it is guaranteed that <code> o1 == o2</code> iff <code>deepHashCode(o1) == deepHashCode(o2)</code>.
     * Furthermore, we expect for "most of the cases" we will have
     * <code>deepHashCode(o1) == deepHashCode(o2)</code> iff <code>o1.equals(o2)</code>.
     *
     * @param object the object
     * @return deep hash code computed for <code>object</code>
     * @throws Exception any reflection related exception
     */
    public static long deepHashCode(final Object object) throws Exception {
        VISITED.clear();
        return deepHashCode(W.of(object), InclusionPredicate.INCLUDE_ALL, VISITED);
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

    private static long deepHashCode(final W hashSetSafeObject,
                                     final InclusionPredicate inclusionPredicate,
                                     final Map<W, MutableLong> visited) throws Exception {
        MutableLong result = visited.get(hashSetSafeObject);
        if (result != null) {
            return result.longValue();
        }
        result = new MutableLong(0L);
        visited.put(hashSetSafeObject, result);
        final Object object = hashSetSafeObject.getCore();
        if (object != null) {
            final Class<?> clazz = object.getClass();
            if (isBasicType(clazz) || object instanceof String) {
                result.setValue(object.hashCode());
            } else if (object instanceof Class) {
                result.setValue(((Class) object).getName().hashCode());
            } else if (clazz.isEnum()) {
                result.setValue(((Enum) object).name().hashCode());
            } else {
                result.setValue(clazz.getName().hashCode());
                if (object instanceof Collection) {
                    for (final Object o : (Collection) object) {
                        result.setValue(result.longValue() + deepHashCode(W.of(o), inclusionPredicate, visited));
                    }
                } else if (clazz.isArray()) {
                    final int len = Array.getLength(object);
                    for (int i = 0; i < len; i++) {
                        final W wElement = W.of(Array.get(object, i));
                        result.setValue(result.longValue() * 31L + deepHashCode(wElement, inclusionPredicate, visited));
                    }
                } else {
                    for (final Field field : getAllFieldsList(clazz)) {
                        if (inclusionPredicate.test(field)) {
                            final W wFieldValue = W.of(readField(field, object, true));
                            result.setValue(result.longValue() * 31L + deepHashCode(wFieldValue, inclusionPredicate, visited));
                        }
                    }
                }
            }
        }
        return result.longValue();
    }

    private static boolean isBasicType(Class<?> clazz) {
        return  isWrapperType(clazz) || clazz.isPrimitive();
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static class MutableLong {
        private long value;

        MutableLong(long value) {
            this.value = value;
        }

        long longValue() {
            return this.value;
        }

        void setValue(long value) {
            this.value = value;
        }
    }

	public static <T> void shallowCopy(final T dest, final T src) throws Exception {
		final Class<?> clazz = src.getClass();
		final List<Field> fields = getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = readField(field, src, true);
            writeField(field, dest, fieldValue, true);
        }
	}
}
