package edu.utdallas.objectutils;

import java.util.Arrays;

public class WrappedCharArray implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final char[] value;

    public WrappedCharArray(char[] value) {
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
        WrappedCharArray that = (WrappedCharArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public char[] reify() {
        return this.value;
    }

    @Override
    public char[] reify(ModificationPredicate mutateStatics) {
        return this.value;
    }
}
