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

import edu.utdallas.objectutils.utils.ObjectPrinter;
import edu.utdallas.objectutils.utils.W;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;
import static edu.utdallas.objectutils.ModificationPredicate.NO;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedObject implements Wrapped {
    private static final long serialVersionUID = 1L;

    private static Map<W, List<UnwrappedObjectPlaceholder>> todos;

    private static Map<W, Object> cache;

    protected Class<?> clazz;

    protected Wrapped[] wrappedFieldValues;

    protected final int address;

    public WrappedObject() {
        this.address = Commons.newAddress();
    }

    public WrappedObject(Class<?> clazz, Wrapped[] wrappedFieldValues) {
        this.clazz = clazz;
        this.wrappedFieldValues = wrappedFieldValues;
        this.address = Commons.newAddress();
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
        /* I work around the problem of graph isomorphism by converting the object graphs
        * into strings and the comparing the strings. This is not a true isomorphism
        * checking algorithm as depending on the head node, the result will be different */
        return ObjectPrinter.print(this).equals(ObjectPrinter.print(that));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz);
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Wrapped[] getWrappedFieldValues() {
        return this.wrappedFieldValues;
    }

    public void setWrappedFieldValues(Wrapped[] wrappedFieldValues) {
        this.wrappedFieldValues = wrappedFieldValues;
    }

    private static Object getCache(final Wrapped wrappedObject) {
        return cache.get(W.of(wrappedObject));
    }

    private static void putCache(final Wrapped wrappedObject, final Object reifiedObject) {
        cache.put(W.of(wrappedObject), reifiedObject);
    }

    private static List<UnwrappedObjectPlaceholder> getToDo(final Wrapped wrappedObject) {
        return todos.get(W.of(wrappedObject));
    }

    private static List<UnwrappedObjectPlaceholder> createToDo(final Wrapped wrappedObject) {
        final List<UnwrappedObjectPlaceholder> todoList = new LinkedList<>();
        todos.put(W.of(wrappedObject), todoList);
        return todoList;
    }

    private static void deleteToDo(final Wrapped wrappedObject) {
        todos.remove(W.of(wrappedObject));
    }

    @Override
    public Object unwrap() throws Exception {
        return unwrap(NO);
    }

    @Override
    public Object unwrap(ModificationPredicate mutateStatics) throws Exception {
        synchronized (WrappedObject.class) {
            todos = new HashMap<>();
            cache = new HashMap<>();
            return unwrap0(mutateStatics);
        }
    }

    /* this method intended to avoid lock re-entrance */
    private static Object unwrapMux(final Wrapped wrappedObject,
                                    final ModificationPredicate predicate) throws Exception {
        if (wrappedObject instanceof WrappedObject) {
            return ((WrappedObject) wrappedObject).unwrap0(predicate);
        }
        return wrappedObject.unwrap(predicate);
    }

    private Object unwrap0(final ModificationPredicate mutateStatics) throws Exception {
        final List<UnwrappedObjectPlaceholder> todoList = createToDo(this);
        final boolean shouldModifyStatics = mutateStatics.test(this.clazz);
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
                    final List<UnwrappedObjectPlaceholder> targetObjectToDoList =
                            getToDo(wrappedFieldValue);
                    if (targetObjectToDoList != null) { // cycle?
                        final UnwrappedObjectPlaceholder placeholder =
                                new UnwrappedObjectPlaceholder(rawObject, field);
                        targetObjectToDoList.add(placeholder);
                    } else {
                        value = getCache(wrappedFieldValue);
                        if (value == null) {
                            value = unwrapMux(wrappedFieldValue, mutateStatics);
                        }
                    }
                }
                FieldUtils.writeField(field, rawObject, value, true);
            }
        }
        putCache(this, rawObject);
        deleteToDo(this);
        for (final UnwrappedObjectPlaceholder placeholder : todoList) {
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

    @Override
    public int getAddress() {
        return this.address;
    }
}
