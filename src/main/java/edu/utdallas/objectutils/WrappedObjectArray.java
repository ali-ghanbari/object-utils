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
import java.util.Arrays;

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

    @Override
    public Object unwrap() throws Exception {
        return unwrap(NO);
    }

    @Override
    public Object unwrap(ModificationPredicate mutateStatics) throws Exception {
        final int len = this.value.length;
        final Object array = Array.newInstance(this.componentType, len);
        for (int i = 0; i < len; i++) {
            final Wrapped we = this.value[i];
            Array.set(array, i, we == null ? null : we.unwrap(mutateStatics));
        }
        return array;
    }

    @Override
    public int getAddress() {
        return this.address;
    }
}
