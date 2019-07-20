package edu.utdallas.objectutils;

import java.util.Arrays;

public class WrappedIntArray implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final int[] value;

    public WrappedIntArray(int[] value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedIntArray that = (WrappedIntArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public int[] reify() {
        return this.value;
    }

    @Override
    public int[] reify(ModificationPredicate mutateStatics) {
        return this.value;
    }
}
