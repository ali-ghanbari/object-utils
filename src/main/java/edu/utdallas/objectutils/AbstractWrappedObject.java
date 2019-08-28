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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import static edu.utdallas.objectutils.ModificationPredicate.NO;

public abstract class AbstractWrappedObject implements Wrapped {
    protected final int address;

    protected Class<?> type; // array element type or the object type

    protected Wrapped[] values; // field values or array elements

    /* We are using this to unwrap cyclic object graphs */
    /* We consult this hash-table to find out if we have already unwrapped a wrapped object */
    /* Note that we use object addresses as key for performance reasons */
    private static final Map<Integer, Object> unwrappedObjects;

    static {
        unwrappedObjects = new HashMap<>();
    }

    AbstractWrappedObject(Class<?> type, Wrapped[] values) {
        this.values = values;
        this.type = type;
        this.address = Commons.newAddress();
    }

    /**
     * Returns the type of array elements, in case <code>this</code>
     * represents an object array, or simply object type that is wrapped
     * by <code>this</code>.
     *
     * @return Type
     */
    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * Returns the values of array elements, in case <code>this</code>
     * represents an object array, or field values of the object that
     * is wrapped by <code>this</code>.
     * @return Field values or array element values
     */
    public Wrapped[] getValues() {
        return values;
    }

    public void setValues(Wrapped[] values) {
        this.values = values;
    }

    @Override
    public Object unwrap() throws Exception {
        return unwrap(NO);
    }

    @Override
    public Object unwrap(ModificationPredicate shouldMutate) throws Exception {
        unwrappedObjects.clear();
        return unwrap0(shouldMutate);
    }

    protected abstract Object createRawObject();

    protected abstract void resetCursor(); // objects create iterator; arrays just reset to -1

    protected abstract void advanceCursor();

    protected abstract boolean strictlyImmutableAtCursor();

    protected abstract boolean shouldMutateAtCursor(ModificationPredicate mutateStatics);

    protected abstract void setAtCursor(Object rawObject, Object value) throws Exception;

    private Object unwrap0(ModificationPredicate shouldMutate) throws Exception {
        final Object unwrapped = createRawObject();
        unwrappedObjects.put(this.address, unwrapped);
        resetCursor();
        for (final Wrapped wrappedValue : this.values) {
            advanceCursor();
            while (strictlyImmutableAtCursor()) {
                advanceCursor();
            }
            if (shouldMutateAtCursor(shouldMutate)) {
                final Object value;
                if (wrappedValue instanceof AbstractWrappedObject) {
                    final AbstractWrappedObject wrappedObject = (AbstractWrappedObject) wrappedValue;
                    final Object targetObject = unwrappedObjects.get(wrappedObject.address);
                    if (targetObject != null) { // cycle?
                        value = targetObject;
                    } else {
                        value = wrappedObject.unwrap0(shouldMutate);
                    }
                } else {
                    value = wrappedValue.unwrap(shouldMutate);
                }
                setAtCursor(unwrapped, value);
            }
        }
        return unwrapped;
    }

    @Override
    public int getAddress() {
        return this.address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AbstractWrappedObject that = (AbstractWrappedObject) object;
        final Queue<Wrapped> workList1 = new LinkedList<>();
        final Queue<Wrapped> workList2 = new LinkedList<>();
        Set<Integer> visitedNodes1 = new HashSet<>();
        Set<Integer> visitedNodes2 = new HashSet<>();
        workList1.offer(this);
        workList2.offer(that);
        while (!workList1.isEmpty()) {
            final Wrapped node1 = workList1.poll();
            final Wrapped node2 = workList2.poll();
            /* assume node1 != null && node2 != null */
            if (node1.getClass() != node2.getClass()) {
                return false;
            }
            if (node1 instanceof AbstractWrappedObject) {
                /* this implies node2 instanceof AbstractWrappedObject */
                final AbstractWrappedObject wrappedObject1 =
                        ((AbstractWrappedObject) node1);
                final AbstractWrappedObject wrappedObject2 =
                        ((AbstractWrappedObject) node2);
                if (wrappedObject1.getType() != wrappedObject2.getType()) {
                    return false;
                }
                visitedNodes1.add(wrappedObject1.getAddress());
                visitedNodes2.add(wrappedObject2.getAddress());
                final Wrapped[] values1 = wrappedObject1.getValues();
                final Wrapped[] values2 = wrappedObject2.getValues();
                if (values1.length != values2.length) {
                    return false;
                }
                for (final Wrapped value : wrappedObject1.getValues()) {
                    if (value instanceof AbstractWrappedObject) {
                        if (visitedNodes1.contains(value.getAddress())) {
                            continue;
                        }
                    }
                    workList1.offer(value);
                }
                for (final Wrapped value : values2) {
                    if (value instanceof AbstractWrappedObject) {
                        if (visitedNodes2.contains(value.getAddress())) {
                            continue;
                        }
                    }
                    workList2.offer(value);
                }
            } else if (!node1.equals(node2)) {
                return false;
            }
        }
        return workList2.isEmpty();
    }
}
