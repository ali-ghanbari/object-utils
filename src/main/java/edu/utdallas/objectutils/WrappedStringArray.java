package edu.utdallas.objectutils;

import java.util.Arrays;

public class WrappedStringArray implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final String[] value;

    public WrappedStringArray(String[] value) {
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
        WrappedStringArray that = (WrappedStringArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public String[] reify() {
        return this.value;
    }

    @Override
    public String[] reify(ModificationPredicate mutateStatics) {
        return this.value;
    }
}
