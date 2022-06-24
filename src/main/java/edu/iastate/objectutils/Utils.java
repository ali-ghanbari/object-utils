package edu.iastate.objectutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

final class Utils {
    private static final Set<String> JDK_CLASSES;

    static {
        JDK_CLASSES = new HashSet<>();
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final InputStream is = classloader.getResourceAsStream("jdk-classes.txt");
        try (InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(is));
             BufferedReader reader = new BufferedReader(isr)) {
            for (String line; (line = reader.readLine()) != null; ) {
                JDK_CLASSES.add(line);
            }
        } catch (final IOException ignored) { }
    }

    private Utils() {
        throw new UnsupportedOperationException();
    }

    static boolean isJDKClass(final String className) {
        return JDK_CLASSES.contains(className);
    }

    static boolean isJDKClass(final Class<?> clazz) {
        return isJDKClass(clazz.getName());
    }

    static boolean hasEquals(final Class<?> clazz) {
        try {
            final Method method = clazz.getDeclaredMethod("equals", Object.class);
            return Modifier.isPublic(method.getModifiers()) && method.getReturnType() == boolean.class;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }
}