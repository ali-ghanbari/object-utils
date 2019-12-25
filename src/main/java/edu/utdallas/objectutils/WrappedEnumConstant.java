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
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Wraps an enum constant. Just like a normal object, an enum constant might have fields.
 * Also hash code for these enums does not depend on JVM session.
 *
 * @author Ali Ghanbari
 */
public class WrappedEnumConstant extends WrappedObject {
    private static final long serialVersionUID = 1L;

    private final String name;

    private transient Enum<?> object;

    public WrappedEnumConstant(Enum<?> object, Wrapped[] values) {
        super(object.getClass(), values);
        this.name = object.name();
        this.object = object;
    }

    private Enum<?> getObject() {
        if (this.object == null) {
            for (final Object n : this.type.retrieveClass().getEnumConstants()) {
                final Enum<?> enumObject = (Enum<?>) n;
                if (this.name.equals(enumObject.name())) {
                    this.object = enumObject;
                    break;
                }
            }
        }
        return this.object;
    }

    @Override
    public Object unwrap() throws Exception {
        // unwrapping an enum constant without having a template makes sense only when
        // we have no fields as there is only one instance of any enum constant and we
        // cannot instantiate enums.
        if (this.values.length > 0) {
            throw new IllegalStateException();
        }
        return getObject();
    }

    @Override
    protected Object createRawObject() {
        // since there is only one instance of a given enum object constant
        // we return the only instance. we don't touch the fields of the
        // object. I am not sure if we should have reset the values to
        // JVM default values or not.
        return getObject();
    }

    @Override
    protected boolean staticAtCursor() {
        final Field field = this.fieldAtCursor;
        if (Modifier.isStatic(field.getModifiers())) {
            return true;
        }
        return field.getDeclaringClass() == Enum.class && field.getName().matches("name|ordinal");
    }

    @Override
    public String print() {
        return String.format("ENUM<%s>=%s", this.name, super.print());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrappedEnumConstant)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final WrappedEnumConstant that = (WrappedEnumConstant) o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
