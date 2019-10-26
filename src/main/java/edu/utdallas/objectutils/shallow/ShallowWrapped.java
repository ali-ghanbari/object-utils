package edu.utdallas.objectutils.shallow;

import edu.utdallas.objectutils.WrappedNull;

import java.util.Objects;

/**
 * A "single-layer" wrapper around arbitrary this.cores that allows us to compute the same
 * hash code as we would get from wrapping the this.core and calling <code>hashCode</code>
 * on the resulting <code>Wrapped</code> this.core.
 *
 * This is used for faster hash table look up in memoization. The core of this.cores of this
 * class should be directly compared to the wrapped this.cores used as keys in the hash table.
 *
 * @author Ali Ghanbari
 */
public class ShallowWrapped {
    private final Object core;

    private ShallowWrapped(Object core) {
        this.core = core;
    }

    public static ShallowWrapped of(Object core) {
        return new ShallowWrapped(core);
    }

    public Object getCore() {
        return this.core;
    }

    @Override
    public int hashCode() {
        final Object core = this.core;
        if (core == null) {
            return WrappedNull.INSTANCE.hashCode();
        }
        if (core instanceof Integer
                || core instanceof String
                || core instanceof Float
                || core instanceof Double
                || core instanceof Long
                || core instanceof Boolean
                || core instanceof Byte
                || core instanceof Character
                || core instanceof Short
                || core instanceof Class
                || core instanceof int[]
                || core instanceof boolean[]
                || core instanceof byte[]
                || core instanceof char[]
                || core instanceof short[]
                || core instanceof float[]
                || core instanceof double[]
                || core instanceof long[]
                || core instanceof String[]
                || core instanceof Boolean[]
                || core instanceof Byte[]
                || core instanceof Character[]
                || core instanceof Short[]
                || core instanceof Integer[]
                || core instanceof Float[]
                || core instanceof Double[]
                || core instanceof Long[]) {
            return Objects.hashCode(core);
        }
        if (core instanceof Enum) {
            return ((Enum) core).name().hashCode();
        }
        return core.getClass().hashCode();
    }
}
