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

public class WrappedEnumConstant extends WrappedObject {
    private static final long serialVersionUID = 1L;

    private static final Map<Class<?>, Map<String, Integer>> ADDRESS_MAPS;

    static {
        ADDRESS_MAPS = new HashMap<>();
    }

    private final String name;

    private static int obtainAddress(Class<?> type, String name) {
        Map<String, Integer> nameMap = ADDRESS_MAPS.get(type);
        if (nameMap == null) {
            nameMap = new HashMap<>();
        }
        Integer address = nameMap.get(name);
        if (address == null) {
            address = newAddress();
        }
        nameMap.put(name, address);
        ADDRESS_MAPS.put(type, nameMap);
        return address;
    }

    public WrappedEnumConstant(Class<?> type, Wrapped[] values, String name) {
        super(type, values);
        this.name = name;
        this.address = obtainAddress(type, name);
    }

    @Override
    public Object unwrap() throws Exception {
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
        return Objects.hash(super.hashCode(), this.name);
    }
}
