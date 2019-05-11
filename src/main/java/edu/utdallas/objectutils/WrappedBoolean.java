package edu.utdallas.objectutils;

import java.util.Objects;

/**
 * A wrapped <code>boolean</code> value which is <code>Serializable</code>,mvn
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
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
