package edu.utdallas.objectutils;

import java.util.Objects;

/**
 * A wrapped <code>double</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedDouble implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final double value;

    public WrappedDouble(double value) {
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
        WrappedDouble that = (WrappedDouble) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Double reconstruct() {
        return this.value;
    }

    @Override
    public Double reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
