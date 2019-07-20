package edu.utdallas.objectutils;

import java.util.Arrays;

public class WrappedFloatArray implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final float[] value;

    public WrappedFloatArray(float[] value) {
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
        WrappedFloatArray that = (WrappedFloatArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public float[] reify() {
        return this.value;
    }

    @Override
    public float[] reify(ModificationPredicate mutateStatics) {
        return this.value;
    }
}
