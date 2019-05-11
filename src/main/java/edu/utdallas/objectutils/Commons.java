package edu.utdallas.objectutils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utility functions common to all modules
 *
 * @author Ali Ghanbari
 */
public final class Commons {
    public static boolean strictlyImmutable(final Field field) {
        final int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }
}
