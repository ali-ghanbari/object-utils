package edu.utdallas.objectutils;

import java.util.Objects;

/**
 * A wrapped <code>float</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedFloat implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final float value;

    public WrappedFloat(float value) {
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
        WrappedFloat that = (WrappedFloat) o;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Float reconstruct() {
        return this.value;
    }

    @Override
    public Float reconstruct(boolean updateStaticFields) {
        return reconstruct();
    }
}
