package edu.utdallas.objectutils;

import java.util.Objects;

/**
 * A wrapped <code>char</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedChar implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final char value;

    public WrappedChar(char value) {
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
        WrappedChar that = (WrappedChar) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Character reconstruct() {
        return this.value;
    }

    @Override
    public Character reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
