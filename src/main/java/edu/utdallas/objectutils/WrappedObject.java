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
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class WrappedObject extends AbstractWrappedCompositeObject {
    private static final long serialVersionUID = 1L;

    private static final Pattern PATTERN = Pattern.compile("name|ordinal");

    protected transient Iterator<Field> fieldsIterator;

    protected transient Field fieldAtCursor;

    public WrappedObject(Class<?> type, Wrapped[] values) {
        super(type, values);
    }

    @Override
    protected Object rectifyTemplate(final Object template) {
        if (template == null) {
            return createRawObject();
        }
        if (this.type.retrieveClass() != template.getClass()) {
            return null;
        }
        return template;
    }

    @Override
    protected Object createRawObject() {
        return ObjenesisHelper.newInstance(this.type.retrieveClass());
    }

    @Override
    protected void resetCursor() {
        this.fieldsIterator = getAllFieldsList(this.type.retrieveClass()).iterator();
    }

    @Override
    protected void advanceCursor() {
        this.fieldAtCursor = this.fieldsIterator.next();
    }

    @Override
    protected boolean skippedAtCursor() {
        if (Modifier.isStatic(this.fieldAtCursor.getModifiers())) {
            return true;
        }
        return this.fieldAtCursor.getDeclaringClass() == Enum.class
                && PATTERN.matcher(this.fieldAtCursor.getName()).matches();
    }

    @Override
    protected void setAtCursor(Object rawObject, Object value) throws Exception {
        writeField(this.fieldAtCursor, rawObject, value, true);
    }

    @Override
    protected Object getAtCursor(Object rawObject) throws Exception {
        return readField(this.fieldAtCursor, rawObject, true);
    }

    @Override
    public String print() {
        return ObjectPrinter.print(this);
    }

    @Override
    public double distance(final Wrapped wrapped) {
        // field-by-field distance calculation
        if (!(wrapped instanceof WrappedObject)) {
            return Double.POSITIVE_INFINITY;
        }
        double dist = 0D; // the value to be returned
        final Queue<Wrapped> workList1 = new LinkedList<>();
        final Queue<Wrapped> workList2 = new LinkedList<>();
        final Set<Integer> visitedNodes1 = new HashSet<>();
        final Set<Integer> visitedNodes2 = new HashSet<>();
        workList1.offer(this);
        workList2.offer(wrapped);
        while (!workList1.isEmpty() && workList1.size() == workList2.size()) {
            final Wrapped node1 = workList1.poll();
            final Wrapped node2 = workList2.poll();
            if (node1 == null || node2 == null) {
                if (node1 == node2) { // fields in both objects are ignored
                    continue;
                }
                return Double.POSITIVE_INFINITY;
            }
            /* assert node1 != null && node2 != null */
            if (node1.getClass() != node2.getClass()) {
                return Double.POSITIVE_INFINITY;
            }
            if (node1 instanceof WrappedObject) {
                /* this implies node2 instanceof AbstractWrappedCompositeObject */
                final WrappedObject wrappedObject1 = ((WrappedObject) node1);
                final WrappedObject wrappedObject2 = ((WrappedObject) node2);
                if (!wrappedObject1.type.equals(wrappedObject2.type)) {
                    return Double.POSITIVE_INFINITY;
                }
                visitedNodes1.add(wrappedObject1.getAddress());
                visitedNodes2.add(wrappedObject2.getAddress());
                final Wrapped[] values1 = wrappedObject1.getValues();
                final Wrapped[] values2 = wrappedObject2.getValues();
                // assert values1.length == values2.length
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
            } else {
                dist += node1.distance(node2);
            }
            if (Double.isInfinite(dist)) {
                return dist;
            }
        }
        return workList1.size() == workList2.size() ? dist : Double.POSITIVE_INFINITY;
    }
}
