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

import java.lang.reflect.Array;

import static edu.utdallas.objectutils.Commons.arrayDistance;

/**
 * Base class for all basic-typed one-dimensional arrays
 * (e.g., String[], Integer[], int[], etc).
 *
 * @param <T> Element type
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public abstract class AbstractWrappedBasicArray<T> extends AbstractWrappedReference implements WrappedArray {
    protected final T value;

    protected AbstractWrappedBasicArray(T value) {
        super();
        this.value = value;
    }

    @Override
    public int size() {
        return Array.getLength(this.value);
    }

    @Override
    public double distance(final Wrapped wrapped) {
        if (wrapped instanceof AbstractWrappedBasicArray) {
            final AbstractWrappedBasicArray<?> that =
                    (AbstractWrappedBasicArray<?>) wrapped;
            if (this.value.getClass() != that.value.getClass()) {
                return Double.POSITIVE_INFINITY;
            }
            return arrayDistance(this.value, that.value);
        }
        return Double.POSITIVE_INFINITY;

    }
}
