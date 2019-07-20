package edu.utdallas.objectutils;

import java.util.Arrays;

public class WrappedByteArray implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final byte[] value;

    public WrappedByteArray(byte[] value) {
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
        WrappedByteArray that = (WrappedByteArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public byte[] reify() {
        return this.value;
    }

    @Override
    public byte[] reify(ModificationPredicate mutateStatics) {
        return this.value;
    }
}
