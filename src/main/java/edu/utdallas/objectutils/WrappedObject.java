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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedObject implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final Class<?> clazz;

    private final Wrapped[] wrappedFieldValues;

    public WrappedObject(Class<?> clazz, Wrapped[] wrappedFieldValues) {
        this.clazz = clazz;
        this.wrappedFieldValues = wrappedFieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedObject that = (WrappedObject) o;
        return clazz == that.clazz && Arrays.equals(wrappedFieldValues, that.wrappedFieldValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public Object reconstruct() throws Exception {
        return reconstruct(false);
    }

    @Override
    public Object reconstruct(boolean updateStaticFields) throws Exception {
        final Object rawObject = ObjenesisHelper.newInstance(this.clazz);
        final Iterator<Field> fieldsIterator = FieldUtils.getAllFieldsList(this.clazz).iterator();
        for (final Wrapped wrappedFieldValue : this.wrappedFieldValues) {
            Field field = fieldsIterator.next();
            while (strictlyImmutable(field)) {
                field = fieldsIterator.next();
            }
            if (!Modifier.isStatic(field.getModifiers()) || updateStaticFields) {
                final Object value = wrappedFieldValue == null ? null : wrappedFieldValue.reconstruct();
                FieldUtils.writeField(field, rawObject, value, true);
            }
        }
        return rawObject;
    }

}
