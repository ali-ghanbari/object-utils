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

import edu.utdallas.objectutils.utils.OnDemandClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Base class for object arrays and proper objects.
 * These objects are composite in a sense they have elements/fields that
 * might be objects in turn.
 *
 * @author Ali Ghanbari
 */
public abstract class AbstractWrappedCompositeObject extends AbstractWrappedReference {
    protected OnDemandClass type; // array element type or the object type

    protected Wrapped[] values; // field values or array elements

    /* We are using this to unwrap cyclic object graphs */
    /* We consult this hash-table to find out if we have already unwrapped a wrapped object */
    /* Note that we use object addresses as key for performance reasons */
    private static final Map<Integer, Object> UNWRAPPED_OBJECTS;

    static {
        UNWRAPPED_OBJECTS = new HashMap<>();
    }

    protected AbstractWrappedCompositeObject(Class<?> type, Wrapped[] values) {
        super(); // obtain address
        this.values = values;
        this.type = type == null ? null : OnDemandClass.of(type);
    }

    /**
     * Returns the type of array elements, in case <code>this</code>
     * represents an object array, or simply object type that is wrapped
     * by <code>this</code>.
     *
     * @return Type
     */
    public Class<?> getType() {
        return this.type.retrieveClass();
    }

    public void setType(Class<?> type) {
        this.type = OnDemandClass.of(type);
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
        final Object rawObject = createRawObject();
        return unwrap(rawObject);
    }

    @Override
    public Object unwrap(Object template) throws Exception {
        UNWRAPPED_OBJECTS.clear();
        return unwrap0(template);
    }

    protected abstract Object createRawObject();

    protected abstract void resetCursor(); // objects create iterator; arrays just reset to -1

    protected abstract void advanceCursor();

    protected abstract boolean staticAtCursor(); // arrays always return false

    protected abstract void setAtCursor(Object rawObject, Object value) throws Exception;

    protected abstract Object getAtCursor(Object rawObject) throws Exception;

    private Object unwrap0(final Object template) throws Exception {
        // we always need a genuine template object
        if (template == null) {
            throw new IllegalArgumentException("Template object cannot be null!");
        }
        UNWRAPPED_OBJECTS.put(this.address, template);
        resetCursor();
        for (final Wrapped wrappedValue : this.values) {
            advanceCursor();
            while (staticAtCursor()) {
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
                    } else if (originalObject.getClass() != compositeObject.type.retrieveClass()) {
                        // should we change the type of object?
                        originalObject = compositeObject.createRawObject();
                    }
                    value = compositeObject.unwrap0(originalObject);
                } else { // basic-typed array
                    Object originalObject = getAtCursor(template);
                    if (originalObject == null) {
                        value = wrappedReference.unwrap();
                    } else {
                        value = wrappedReference.unwrap(originalObject);
                    }
                }
                UNWRAPPED_OBJECTS.put(wrappedReference.address, value);
            } else {
                // if the wrapped value represents null or a primitive-typed object
                value = wrappedValue.unwrap();
            }
            setAtCursor(template, value);
        }
        return template;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
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
                final Wrapped[] values1 = wrappedObject1.getValues();
                final Wrapped[] values2 = wrappedObject2.getValues();
                if (values1.length != values2.length) {
                    return false;
                }
                for (int i = 0; i < values1.length; i++) {
                    final Wrapped value = values1[i];
                    if (value instanceof AbstractWrappedCompositeObject) {
                        if (visitedNodes1.contains(value.getAddress())) {
                            continue;
                        }
                    }
                    workList1.offer(value);
                    workList2.offer(values2[i]);
                }
            } else if (!node1.equals(node2)) {
                return false;
            }
        }
        return workList2.isEmpty();
    }
}
