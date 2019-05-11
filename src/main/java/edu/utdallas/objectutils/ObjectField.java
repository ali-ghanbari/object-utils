package edu.utdallas.objectutils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a field inside a class.
 *
 * @author Ali Ghanbari
 */
public class ObjectField implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Wrapped value;

    public ObjectField(String name, Wrapped value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Wrapped getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectField field = (ObjectField) o;
        return name.equals(field.name) && Objects.equals(value, field.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
