package edu.utdallas.objectutils;

import java.util.Objects;

public class WrappedString implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final String value;

    public WrappedString(String value) {
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
        WrappedString that = (WrappedString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String reconstruct() {
        return this.value;
    }

    @Override
    public String reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
