package edu.utdallas.objectutils.shallow;

import edu.utdallas.objectutils.WrappedNull;

import java.util.Arrays;
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
                || core instanceof Class) {
            return Objects.hashCode(core);
        }
        if (core instanceof int[]) {
            return Arrays.hashCode((int[]) core);
        }
        if (core instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) core);
        }
        if (core instanceof byte[]) {
            return Arrays.hashCode((byte[]) core);
        }
        if (core instanceof char[]) {
            return Arrays.hashCode((char[]) core);
        }
        if (core instanceof short[]) {
            return Arrays.hashCode((short[]) core);
        }
        if (core instanceof float[]) {
            return Arrays.hashCode((float[]) core);
        }
        if (core instanceof double[]) {
            return Arrays.hashCode((double[]) core);
        }
        if (core instanceof long[]) {
            return Arrays.hashCode((long[]) core);
        }
        if (core instanceof String[]) {
            return Arrays.hashCode((String[]) core);
        }
        if (core instanceof Boolean[]) {
            return Arrays.hashCode((Boolean[]) core);
        }
        if (core instanceof Byte[]) {
            return Arrays.hashCode((Byte[]) core);
        }
        if (core instanceof Character[]) {
            return Arrays.hashCode((Character[]) core);
        }
        if (core instanceof Short[]) {
            return Arrays.hashCode((Short[]) core);
        }
        if (core instanceof Integer[]) {
            return Arrays.hashCode((Integer[]) core);
        }
        if (core instanceof Float[]) {
            return Arrays.hashCode((Float[]) core);
        }
        if (core instanceof Double[]) {
            return Arrays.hashCode((Double[]) core);
        }
        if (core instanceof Long[]) {
            return Arrays.hashCode((Long[]) core);
        }
        if (core instanceof Enum) {
            return ((Enum) core).name().hashCode();
        }
        final Class<?> coreClass = core.getClass();
        if (coreClass.isArray()) {
            return coreClass.getComponentType().hashCode();
        }
        return coreClass.hashCode();
    }
}
