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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;
import static edu.utdallas.objectutils.Commons.getObjectId;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedObject implements Wrapped {
    private static final long serialVersionUID = 1L;

    private static final ModificationPredicate NO = new ModificationPredicate() {
        @Override
        public boolean shouldModifyStaticFields(Class<?> clazz) {
            return false;
        }
    };

    private static Map<String, List<ReifiedObjectPlaceholder>> todos;

    private static Map<String, Object> cache;

    protected Class<?> clazz;

    protected Wrapped[] wrappedFieldValues;

    public WrappedObject() {

    }

    public WrappedObject(Class<?> clazz, Wrapped[] wrappedFieldValues) {
        this.clazz = clazz;
        this.wrappedFieldValues = wrappedFieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedObject that = (WrappedObject) o;
        return clazz == that.clazz && Arrays.equals(wrappedFieldValues, that.wrappedFieldValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Wrapped[] getWrappedFieldValues() {
        return wrappedFieldValues;
    }

    public void setWrappedFieldValues(Wrapped[] wrappedFieldValues) {
        this.wrappedFieldValues = wrappedFieldValues;
    }

    private static Object getCache(final Wrapped wrappedObject) {
        final String key = getObjectId(wrappedObject);
        return cache.get(key);
    }

    private static void putCache(final Wrapped wrappedObject, final Object reifiedObject) {
        final String key = getObjectId(wrappedObject);
        cache.put(key, reifiedObject);
    }

    private static List<ReifiedObjectPlaceholder> getToDo(final Wrapped wrappedObject) {
        final String key = getObjectId(wrappedObject);
        return todos.get(key);
    }

    private static List<ReifiedObjectPlaceholder> createToDo(final Wrapped wrappedObject) {
        final String key = getObjectId(wrappedObject);
        final List<ReifiedObjectPlaceholder> todoList = new LinkedList<>();
        todos.put(key, todoList);
        return todoList;
    }

    private static void deleteToDo(final Wrapped wrappedObject) {
        final String key = getObjectId(wrappedObject);
        todos.remove(key);
    }

    @Override
    public Object reify() throws Exception {
        return reify(NO);
    }

    @Override
    public Object reify(final ModificationPredicate predicate) throws Exception {
        synchronized (WrappedObject.class) {
            todos = new HashMap<>();
            cache = new HashMap<>();
            return reify0(predicate);
        }
    }

    /* this method intended to avoid lock re-entrance */
    private static Object reifyMux(final Wrapped wrappedObject,
                                   final ModificationPredicate predicate) throws Exception {
        if (wrappedObject instanceof WrappedObject) {
            return ((WrappedObject) wrappedObject).reify0(predicate);
        }
        return wrappedObject.reify(predicate);
    }

    private Object reify0(final ModificationPredicate predicate) throws Exception {
        final List<ReifiedObjectPlaceholder> todoList = createToDo(this);
        final boolean shouldModifyStatics = predicate.shouldModifyStaticFields(this.clazz);
        final Object rawObject = ObjenesisHelper.newInstance(this.clazz);
        final Iterator<Field> fieldsIterator = FieldUtils.getAllFieldsList(this.clazz).iterator();
        for (final Wrapped wrappedFieldValue : this.wrappedFieldValues) {
            Field field = fieldsIterator.next();
            while (strictlyImmutable(field)) {
                field = fieldsIterator.next();
            }
            if (!Modifier.isStatic(field.getModifiers()) || shouldModifyStatics) {
                Object value = null;
                if (wrappedFieldValue != null) {
                    final List<ReifiedObjectPlaceholder> targetObjectToDoList =
                            getToDo(wrappedFieldValue);
                    if (targetObjectToDoList != null) { // cycle?
                        final ReifiedObjectPlaceholder placeholder =
                                new ReifiedObjectPlaceholder(rawObject, field);
                        targetObjectToDoList.add(placeholder);
                    } else {
                        value = getCache(wrappedFieldValue);
                        if (value == null) {
                            value = reifyMux(wrappedFieldValue, predicate);
                        }
                    }
                }
                FieldUtils.writeField(field, rawObject, value, true);
            }
        }
        putCache(this, rawObject);
        deleteToDo(this);
        for (final ReifiedObjectPlaceholder placeholder : todoList) {
            placeholder.substitute(rawObject);
        }
        return rawObject;
    }

    WrappedObjectPlaceholder createPlaceholder(final int fieldIndex) {
        return new WrappedObjectPlaceholderImp(fieldIndex);
    }

    protected class WrappedObjectPlaceholderImp implements WrappedObjectPlaceholder {
        final int fieldIndex;

        public WrappedObjectPlaceholderImp(final int fieldIndex) {
            this.fieldIndex = fieldIndex;
        }

        @Override
        public void substitute(Wrapped wrappedObject) {
            WrappedObject.this.wrappedFieldValues[this.fieldIndex] = wrappedObject;
        }
    }
}
