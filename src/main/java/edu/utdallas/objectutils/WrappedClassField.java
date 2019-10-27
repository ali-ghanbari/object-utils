package edu.utdallas.objectutils;

/*
 * #%L
 * object-utils
 * %%
 * Copyright (C) 2019 The University of Texas at Dallas
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Field;

/**
 * Represents a wrapped java reflection field
 *
 * @author Ali Ghanbari
 */
public class WrappedClassField implements Wrapped {
    private final Class<?> declaringClass;

    private final String fieldName;

    public WrappedClassField(Field field) {
        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
    }

    @Override
    public Object unwrap(ModificationPredicate shouldMutate) throws Exception {
        return unwrap();
    }

    @Override
    public Object unwrap() throws Exception {
        return this.declaringClass.getDeclaredField(this.fieldName);
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
            return String.format("Field<%s::%s>", this.declaringClass.getName(), this.fieldName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean coreEquals(Object core) {
        if (core instanceof Field) {
            final Field coreField = (Field) core;
            return coreField.getDeclaringClass() == this.declaringClass
                    && coreField.getName().equals(this.fieldName);
        }
        return false;
    }
}
