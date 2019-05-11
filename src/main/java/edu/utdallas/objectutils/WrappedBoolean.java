package edu.utdallas.objectutils;

import java.util.Objects;

public class WrappedBoolean implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final boolean value;

    public WrappedBoolean(boolean value) {
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
        WrappedBoolean that = (WrappedBoolean) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Boolean reconstruct() {
        return this.value;
    }

    @Override
    public Boolean reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
