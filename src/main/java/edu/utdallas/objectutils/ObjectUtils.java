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
import org.apache.commons.lang3.reflect.FieldUtils;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic object utilities
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
     */
    public static long deepHashCode(final Object object) {
        VISITED.clear();
        return deepHashCode(W.of(object), VISITED);
    }

    private static long deepHashCode(final W hashSetSafeObject, final Map<W, MutableLong> visited) {
        MutableLong result = visited.get(hashSetSafeObject);
        if (result != null) {
            return result.longValue();
        }
        result = new MutableLong(0L);
        visited.put(hashSetSafeObject, result);
        final Object object = hashSetSafeObject.getCore();
        if (object != null) {
            final Class<?> clazz = object.getClass();
            if (isBasicType(clazz) || object instanceof Class) {
                result.setValue(object.hashCode());
            } else {
                result.setValue(1L);
                final WIterator iterator = clazz.isArray() ?
                        new ArrayWIterator(object) : new ObjectFieldWIterator(object, clazz);
                while (iterator.hasNext()) {
                    result.setValue(result.longValue() * 31L + deepHashCode(iterator.nextW(), visited));
                }
            }
        }
        return result.longValue();
    }

    private abstract static class WIterator implements Iterator<W> {
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public W next() {
            throw new UnsupportedOperationException();
        }

        abstract W nextW();
    }

    private static class ArrayWIterator extends WIterator {
        private final Object object;

        private final int len;

        private int cursor;

        ArrayWIterator(final Object object) {
            this.cursor = 0;
            this.len = Array.getLength(object);
            this.object = object;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.len;
        }

        @Override
        public W nextW() {
            return W.of(Array.get(this.object, this.cursor++));
        }
    }

    private static class ObjectFieldWIterator extends WIterator {
        private final Object object;

        private final Field[] fields;

        private int cursor;

        public ObjectFieldWIterator(final Object object, final Class<?> clazz) {
            this.object = object;
            this.fields = FieldUtils.getAllFields(clazz);
            this.cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.fields.length;
        }

        @Override
        public W nextW() {
            try {
                return W.of(FieldUtils.readField(this.fields[this.cursor++], this.object, true));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error();
            }
        }
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

	public static <T> void shallowCopy(final T dest, final T src) throws Exception {
		final Class<?> clazz = src.getClass();
		final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = FieldUtils.readField(field, src, true);
            FieldUtils.writeField(field, dest, fieldValue, true);
        }
	}
}
