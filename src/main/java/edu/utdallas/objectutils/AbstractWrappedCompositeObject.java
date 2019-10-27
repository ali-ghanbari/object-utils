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
import java.util.Queue;
import java.util.Set;

import static edu.utdallas.objectutils.ModificationPredicate.NO;

/**
 * Base class for object arrays and proper objects.
 * These objects are composite in a sense they have elements/fields that
 * might be objects in turn.
 *
 * @author Ali Ghanbari
 */

public abstract class AbstractWrappedCompositeObject extends AbstractWrappedReference {
    protected Class<?> type; // array element type or the object type

    protected Wrapped[] values; // field values or array elements

    /* We are using this to unwrap cyclic object graphs */
    /* We consult this hash-table to find out if we have already unwrapped a wrapped object */
    /* Note that we use object addresses as key for performance reasons */
    private static final Map<Integer, Object> UNWRAPPED_OBJECTS;

    static {
        UNWRAPPED_OBJECTS = new HashMap<>();
    }

    AbstractWrappedCompositeObject(Class<?> type, Wrapped[] values) {
        super(); // obtain address
        this.values = values;
        this.type = type;
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
        final Object rawObject = createRawObject();
        return unwrap(rawObject, shouldMutate);
    }

    @Override
    public Object unwrap(Object template) throws Exception {
        return unwrap(template, NO);
    }

    @Override
    public Object unwrap(Object template, ModificationPredicate shouldMutate) throws Exception {
        UNWRAPPED_OBJECTS.clear();
        return unwrap0(template, shouldMutate);
    }

    protected abstract Object createRawObject();

    protected abstract void resetCursor(); // objects create iterator; arrays just reset to -1

    protected abstract void advanceCursor();

    protected abstract boolean strictlyImmutableAtCursor();

    protected abstract boolean shouldMutateAtCursor(ModificationPredicate mutateStatics);

    protected abstract void setAtCursor(Object rawObject, Object value) throws Exception;

    protected abstract Object getAtCursor(Object rawObject) throws Exception;

    private Object unwrap0(final Object template,
                           final ModificationPredicate shouldMutate) throws Exception {
        // we always need a genuine template object
        if (template == null) {
            throw new IllegalArgumentException("Template object cannot be null!");
        }
        UNWRAPPED_OBJECTS.put(this.address, template);
        resetCursor();
        for (final Wrapped wrappedValue : this.values) {
            advanceCursor();
            while (strictlyImmutableAtCursor()) {
                advanceCursor();
            }
            // we ignore some fields based on what the client has requested.
            // ignored fields shall continue to have their values they used to have
            // in the template object. this means that if the template object is not
            // provided by the client, those fields shall have their default values.
            // note that an array element cannot be 'null' as filtering only
            // applies to fields.
            if (wrappedValue == null) {
                continue;
            }
            if (shouldMutateAtCursor(shouldMutate)) {
                final Object value;
                if (wrappedValue instanceof AbstractWrappedReference) {
                    final AbstractWrappedReference wrappedReference =
                            (AbstractWrappedReference) wrappedValue;
                    final Object targetObject = UNWRAPPED_OBJECTS.get(wrappedReference.address);
                    if (targetObject != null) { // reusable object?
                        value = targetObject;
                    } else if (wrappedReference instanceof AbstractWrappedCompositeObject) {
                        final AbstractWrappedCompositeObject compositeObject =
                                (AbstractWrappedCompositeObject) wrappedReference;
                        Object originalObject = getAtCursor(template);
                        // assert wrapped value does not represent null value
                        if (originalObject == null) {
                            originalObject = compositeObject.createRawObject();
                        } else if (originalObject.getClass() != compositeObject.type) {
                            // should we change the type of object?
                            originalObject = compositeObject.createRawObject();
                        }
                        value = compositeObject.unwrap0(originalObject, shouldMutate);
                    } else { // basic-typed array
                        Object originalObject = getAtCursor(template);
                        if (originalObject == null) {
                            value = wrappedReference.unwrap(shouldMutate);
                        } else {
                            value = wrappedReference.unwrap(originalObject, shouldMutate);
                        }
                    }
                    UNWRAPPED_OBJECTS.put(wrappedReference.address, value);
                } else {
                    // if the wrapped value represents null or a primitive-typed object
                    value = wrappedValue.unwrap(shouldMutate);
                }
                setAtCursor(template, value);
            }
        }
        return template;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    //FIXME: simplify the algorithm; I think we can get rid of one of the sets and loops
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AbstractWrappedCompositeObject that = (AbstractWrappedCompositeObject) object;
        final Queue<Wrapped> workList1 = new LinkedList<>();
        final Queue<Wrapped> workList2 = new LinkedList<>();
        final Set<Integer> visitedNodes1 = new HashSet<>();
        final Set<Integer> visitedNodes2 = new HashSet<>();
        workList1.offer(this);
        workList2.offer(that);
        while (!workList1.isEmpty()) {
            final Wrapped node1 = workList1.poll();
            final Wrapped node2 = workList2.poll();
            if (node1 == null || node2 == null) {
                if (node1 == node2) { // fields in both objects are ignored
                    continue;
                } else {
                    return false;
                }
            }
            /* assume node1 != null && node2 != null */
            if (node1.getClass() != node2.getClass()) {
                return false;
            }
            if (node1 instanceof AbstractWrappedCompositeObject) {
                /* this implies node2 instanceof AbstractWrappedObject */
                final AbstractWrappedCompositeObject wrappedObject1 =
                        ((AbstractWrappedCompositeObject) node1);
                final AbstractWrappedCompositeObject wrappedObject2 =
                        ((AbstractWrappedCompositeObject) node2);
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
                for (final Wrapped value : values1) {
                    if (value instanceof AbstractWrappedCompositeObject) {
                        if (visitedNodes1.contains(value.getAddress())) {
                            continue;
                        }
                    }
                    workList1.offer(value);
                }
                for (final Wrapped value : values2) {
                    if (value instanceof AbstractWrappedCompositeObject) {
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

    // checks if the type of "core" matches the type of the wrapped object or the
    // the type of elements of the wrapped array
    protected abstract boolean coreTypeCheck(Object core);

    @Override
    public boolean coreEquals(final Object core) {
        if (core == null) {
            return false;
        }
        final Queue<Wrapped> workList1 = new LinkedList<>();
        final Queue<Object> workList2 = new LinkedList<>();
        final Set<Integer> visitedNodes = new HashSet<>();
        workList1.offer(this);
        workList2.offer(core);
        while (!workList1.isEmpty()) {
            final Wrapped node1 = workList1.poll();
            final Object node2 = workList2.poll();
            if (node1 == null) {
                // the field in wrapped object is ignored, so we ignore the
                // corresponding field in the provided core object
                continue;
            }
            if (node1 instanceof AbstractWrappedCompositeObject) {
                if (node2 == null || !coreTypeCheck(node2)) {
                    return false;
                }
                final AbstractWrappedCompositeObject wrappedObject = ((AbstractWrappedCompositeObject) node1);
                visitedNodes.add(wrappedObject.getAddress());
                final Wrapped[] wrappedValues = wrappedObject.getValues();
                resetCursor();
                try {
                    for (final Wrapped wrappedValue : wrappedValues) {
                        advanceCursor();
                        while (strictlyImmutableAtCursor()) {
                            advanceCursor();
                        }
                        final Object value = getAtCursor(node2);
                        if (wrappedValue instanceof AbstractWrappedCompositeObject) {
                            if (visitedNodes.contains(wrappedValue.getAddress())) {
                                continue;
                            }
                        }
                        workList1.offer(wrappedValue);
                        workList2.offer(value);
                    }
                } catch (Exception e) {
                    return false;
                }
            } else if (!node1.coreEquals(node2)) {
                return false;
            }
        }
        return workList2.isEmpty();
    }
}
