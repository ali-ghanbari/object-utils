package edu.iastate.objectutils;

import java.lang.reflect.Array;
import java.util.Objects;

class PrimitiveOrWrapperArrayProxy implements TerminalProxy {
    private static final long serialVersionUID = 1L;

    final Object array;

    public PrimitiveOrWrapperArrayProxy(final Object array) {
        final int length = Array.getLength(array);
        this.array = array;

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrimitiveOrWrapperArrayProxy)) {
            return false;
        }
        final PrimitiveOrWrapperArrayProxy that = (PrimitiveOrWrapperArrayProxy) o;
        final int length = Array.getLength(this.array);
        if (length != Array.getLength(that.array)) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!Objects.equals(Array.get(this.array, i), Array.get(that.array, i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String print() {
        final StringBuilder sb = new StringBuilder();
        final int length = Array.getLength(this.array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(Array.get(this.array, i));
        }
        return "[" + sb + "]";
    }
}
