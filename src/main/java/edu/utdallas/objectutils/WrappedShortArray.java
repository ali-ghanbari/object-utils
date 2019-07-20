package edu.utdallas.objectutils;

import java.util.Arrays;

public class WrappedShortArray implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final short[] value;

    public WrappedShortArray(short[] value) {
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
        WrappedShortArray that = (WrappedShortArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public short[] reify() {
        return this.value;
    }

    @Override
    public short[] reify(ModificationPredicate mutateStatics) {
        return this.value;
    }
}
