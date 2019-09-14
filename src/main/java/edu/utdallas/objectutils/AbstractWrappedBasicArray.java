package edu.utdallas.objectutils;

import java.util.Arrays;

/**
 * Base class for all basic-typed one-dimensional arrays
 * (e.g., String[], Integer[], int[], etc).
 *
 * @param <T> Element type
 * @author Ali Ghanbari
 */

public abstract class AbstractWrappedBasicArray<T> extends AbstractWrappedReference implements WrappedArray {
    protected final T value;

    protected AbstractWrappedBasicArray(T value) {
        super();
        this.value = value;
    }
}
