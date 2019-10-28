package edu.utdallas.objectutils.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class HashCodeUtils {
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    private HashCodeUtils() {

    }

    public static long deepHashCode(final Object object) {
        if (object == null) {
            return 0;
        }
        final Class<?> clazz = object.getClass();
        if (isBasicType(clazz)) {
            return Objects.hashCode(object);
        }
        return 0;
    }

    private static boolean isBasicType(Class<?> clazz) {
        return  clazz.isPrimitive() || isWrapperType(clazz) || clazz == String.class || clazz == Class.class;
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }
}
