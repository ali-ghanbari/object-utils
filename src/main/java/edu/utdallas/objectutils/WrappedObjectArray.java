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

import java.lang.reflect.Array;

/**
 * Represents an array of objects or a multi-dimensional array.
 * The objects might point to the array itself or some wrapped number.
 *
 * @author Ali Ghanbari
 */
public class WrappedObjectArray extends AbstractWrappedCompositeObject implements WrappedArray {
    private static final long serialVersionUID = 1L;

    private transient int cursor;

    public WrappedObjectArray(Class<?> type, Wrapped[] values) {
        super(type, values);
    }

    @Override
    protected Object createRawObject() {
        return Array.newInstance(this.type, this.values.length);
    }

    @Override
    protected void resetCursor() {
        this.cursor = -1;
    }

    @Override
    protected void advanceCursor() {
        this.cursor++;
    }

    @Override
    protected boolean strictlyImmutableAtCursor() {
        return false;
    }

    @Override
    protected boolean shouldMutateAtCursor(ModificationPredicate mutateStatics) {
        return true;
    }

    @Override
    protected void setAtCursor(Object array, Object value) throws Exception {
        Array.set(array, this.cursor, value);
    }

    @Override
    protected Object getAtCursor(Object rawObject) throws Exception {
        return Array.get(rawObject, this.cursor);
    }

    @Override
    public String print() {
        return ObjectPrinter.print(this);
    }

    @Override
    protected boolean coreTypeCheck(Object core) {
        if (core == null) {
            return false;
        }
        final Class<?> coreClass = core.getClass();
        return coreClass.isArray() && coreClass.getComponentType() == this.type
                && Array.getLength(core) == this.values.length;
    }
}
