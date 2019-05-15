package edu.utdallas.objectutils;

public interface ModificationPredicate {
    boolean shouldModifyStaticFields(Class<?> clazz);
}
