package edu.utdallas.objectutils;

import java.util.Objects;

public class WrappedLong implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final long value;

    public WrappedLong(long value) {
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
        WrappedLong that = (WrappedLong) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Long reconstruct() {
        return this.value;
    }

    @Override
    public Long reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
