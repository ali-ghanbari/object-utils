package edu.utdallas.objectutils;

import java.lang.reflect.Field;

/**
 * Represents a wrapped java reflection field
 *
 * @author Ali Ghanbari
 */
public class WrappedClassField extends WrappedObject {
    private final String fieldName;

    public WrappedClassField(Field field) {
        super(field.getDeclaringClass(), null);
        this.fieldName = field.getName();
    }

    @Override
    public Object unwrap(ModificationPredicate shouldMutate) throws Exception {
        return unwrap();
    }

    @Override
    public Object unwrap() throws Exception {
        return this.type.getDeclaredField(this.fieldName);
    }

    @Override
    public Object unwrap(Object template) throws Exception {
        return unwrap();
    }

    @Override
    public Object unwrap(Object template, ModificationPredicate shouldMutate) throws Exception {
        return unwrap();
    }

    @Override
    public String print() {
        try {
            return this.type.getName() + "::" + this.fieldName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
