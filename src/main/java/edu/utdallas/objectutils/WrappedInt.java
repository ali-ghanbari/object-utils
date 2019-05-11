package edu.utdallas.objectutils;

import java.util.Objects;

/**
 * A wrapped <code>int</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedInt implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final int value;

    public WrappedInt(int value) {
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
        WrappedInt that = (WrappedInt) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Integer reconstruct() {
        return this.value;
    }

    @Override
    public Integer reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
