package edu.utdallas.objectutils;

import java.util.Objects;

public class WrappedByte implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final byte value;

    public WrappedByte(byte value) {
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
        WrappedByte that = (WrappedByte) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Byte reconstruct() {
        return this.value;
    }

    @Override
    public Byte reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
