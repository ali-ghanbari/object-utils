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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static edu.utdallas.objectutils.Commons.newAddress;
import static edu.utdallas.objectutils.Commons.strictlyImmutable;

/**
 * Wraps an enum constant. Just like a normal object, an enum constant might have fields.
 *
 * @author Ali Ghanbari
 */
public class WrappedEnumConstant extends WrappedObject {
    private static final long serialVersionUID = 1L;

    private static final Map<Enum, Integer> ADDRESS_MAP;

    static {
        ADDRESS_MAP = new HashMap<>();
    }

    private final String name;

    public WrappedEnumConstant(Enum object, Wrapped[] values) {
        super(object.getClass(), values);
        this.name = object.name();
        Integer address = ADDRESS_MAP.get(object);
        if (address == null) {
            address = newAddress();
            ADDRESS_MAP.put(object, address);
        }
        this.address = address;
    }

    @Override
    public Object unwrap() throws Exception {
        // unwrapping an enum constant without having a template makes sense only when
        // we have no fields as there is only one instance of any enum constant and we
        // cannot instantiate enums.
        if (this.values.length > 0) {
            throw new IllegalStateException();
        }
        for (final Object n : this.type.getEnumConstants()) {
            if (this.name.equals(((Enum) n).name())) {
                return n;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public Object unwrap(ModificationPredicate shouldMutate) throws Exception {
        return unwrap();
    }

    @Override
    protected Object createRawObject() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean strictlyImmutableAtCursor() {
        final Field field = this.fieldAtCursor;
        return strictlyImmutable(field) || (field.getDeclaringClass() == Enum.class && field.getName().matches("name|ordinal"));
    }

    @Override
    public String print() {
        return this.name + ":" + super.print();
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
        WrappedEnumConstant that = (WrappedEnumConstant) o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    @Override
    protected boolean coreTypeCheck(Object core) {
        if (core instanceof Enum<?>) {
            return core.getClass() == this.type && ((Enum<?>) core).name().equals(this.name);
        }
        return false;
    }
}