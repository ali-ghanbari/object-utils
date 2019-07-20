package edu.utdallas.objectutils;

import java.util.Arrays;

public class WrappedBooleanArray implements Wrapped {
    private static final long serialVersionUID = 1L;

    private boolean[] value;

    public WrappedBooleanArray(boolean[] value) {
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
        WrappedBooleanArray that = (WrappedBooleanArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public boolean[] reify() {
        return this.value;
    }

    @Override
    public boolean[] reify(ModificationPredicate mutateStatics) {
        return this.value;
    }
}
