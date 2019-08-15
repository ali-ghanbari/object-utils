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

import java.lang.reflect.Array;
import java.util.*;

import static edu.utdallas.objectutils.ModificationPredicate.NO;

/**
 * Represents an array of objects or a multi-dimensional array.
 * The objects might point to the array itself or some wrapped number.
 *
 * @author Ali Ghanbari
 */
public class WrappedObjectArray implements WrappedArray {
    private static final long serialVersionUID = 1L;

    protected final int address;

    protected final Class<?> componentType;

    protected final Wrapped[] value;

    private static Map<W, List<UnwrappedObjectArrayPlaceholder>> todos;

    private static Map<W, Object> cache;

    public WrappedObjectArray(Class<?> componentType, Wrapped[] value) {
        this.value = value;
        this.componentType = componentType;
        this.address = Commons.newAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrappedObjectArray)) {
            return false;
        }
        WrappedObjectArray that = (WrappedObjectArray) o;
        /* I work around the problem of graph isomorphism by converting the object graphs
         * into strings and the comparing the strings. This is not a true isomorphism
         * checking algorithm as depending on the head node, the result will be different */
        return ObjectPrinter.print(this).equals(ObjectPrinter.print(that));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    public Class<?> getComponentType() {
        return componentType;
    }

    public Wrapped[] getValue() {
        return value;
    }

    private static Object getCache(final Wrapped wrapped) {
        return cache.get(W.of(wrapped));
    }

    private static void putCache(final Wrapped wrapped, final Object unwrapped) {
        cache.put(W.of(wrapped), unwrapped);
    }

    private static List<UnwrappedObjectArrayPlaceholder> getToDo(final Wrapped wrapped) {
        return todos.get(W.of(wrapped));
    }

    private static List<UnwrappedObjectArrayPlaceholder> createToDo(final Wrapped wrapped) {
        final List<UnwrappedObjectArrayPlaceholder> todoList = new LinkedList<>();
        todos.put(W.of(wrapped), todoList);
        return todoList;
    }

    private static void deleteToDo(final Wrapped wrapped) {
        todos.remove(W.of(wrapped));
    }

    @Override
    public Object unwrap() throws Exception {
        return unwrap(NO);
    }

    @Override
    public Object unwrap(ModificationPredicate mutateStatics) throws Exception {
//        final int len = this.value.length;
//        final Object array = Array.newInstance(this.componentType, len);
//        for (int i = 0; i < len; i++) {
//            final Wrapped we = this.value[i];
//            Array.set(array, i, we == null ? null : we.unwrap(mutateStatics));
//        }
//        return array;
        todos = new HashMap<>();
        cache = new HashMap<>();

        return unwrap0(mutateStatics);
    }

    public Object unwrap0(ModificationPredicate mutateStatics) throws Exception {
        final List<UnwrappedObjectArrayPlaceholder> todoList = createToDo(this);
        final int len = this.value.length;
        final Object array = Array.newInstance(this.componentType, len);

    }

    WrappedPlaceholder createPlaceholder(final int elementIndex) {
        return new WrappedObjectArrayPlaceholder(elementIndex);
    }

    protected class WrappedObjectArrayPlaceholder implements WrappedPlaceholder {
        final int elementIndex;

        public WrappedObjectArrayPlaceholder(final int elementIndex) {
            this.elementIndex = elementIndex;
        }

        @Override
        public void substitute(Wrapped wrappedObjectArray) {
            WrappedObjectArray.this.value[this.elementIndex] = wrappedObjectArray;
        }
    }

    protected static class UnwrappedObjectArrayPlaceholder implements UnwrappedPlaceholder {
        /* this is the array whose index'th index is going to be replaced by some unwrapped object */
        final Object[] sourceArray;

        final int index;

        public UnwrappedObjectArrayPlaceholder(final Object[] sourceArray, final int index) {
            this.sourceArray = sourceArray;
            this.index = index;
        }

        @Override
        public void substitute(final Object unwrappedTargetObject) throws Exception {
            this.sourceArray[this.index] = unwrappedTargetObject;
        }
    }

    @Override
    public int getAddress() {
        return this.address;
    }
}
