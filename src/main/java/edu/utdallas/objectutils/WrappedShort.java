package edu.utdallas.objectutils;

import java.util.Objects;

public class WrappedShort implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final short value;

    public WrappedShort(short value) {
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
        WrappedShort that = (WrappedShort) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Short reconstruct() {
        return this.value;
    }

    @Override
    public Short reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
