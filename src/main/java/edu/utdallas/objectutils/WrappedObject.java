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

import edu.utdallas.objectutils.utils.ObjectPrinter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */

public class WrappedObject extends AbstractWrappedObject {
    private static final long serialVersionUID = 1L;

    private transient Iterator<Field> fieldsIterator;

    private transient Field fieldAtCursor;

    public WrappedObject(Class<?> type, Wrapped[] values) {
        super(type, values);
    }

    @Override
    protected Object createRawObject() {
        return ObjenesisHelper.newInstance(this.type);
    }

    @Override
    protected void resetCursor() {
        this.fieldsIterator = FieldUtils.getAllFieldsList(this.type).iterator();
    }

    @Override
    protected void advanceCursor() {
        this.fieldAtCursor = this.fieldsIterator.next();
    }

    @Override
    protected boolean strictlyImmutableAtCursor() {
        return strictlyImmutable(this.fieldAtCursor);
    }

    @Override
    protected boolean shouldMutateAtCursor(ModificationPredicate mutateStatics) {
        final Field field = this.fieldAtCursor;
        return !Modifier.isStatic(field.getModifiers()) || mutateStatics.test(field);
    }

    @Override
    protected void setAtCursor(Object rawObject, Object value) throws Exception {
        FieldUtils.writeField(this.fieldAtCursor, rawObject, value, true);
    }

//    @Override
//    protected UnwrappedPlaceholder createUnwrappedPlaceholderForCursor(Object unwrapped) {
//        return new UnwrappedObjectPlaceholder(unwrapped, this.fieldAtCursor);
//    }

//    WrappedPlaceholder createWrappedPlaceholder(final int fieldIndex) {
//        return new ObjectWrappedPlaceholder(fieldIndex);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedObject that = (WrappedObject) o;
        /* I work around the problem of graph isomorphism by converting the object graphs
         * into strings and the comparing the strings. This is not a true isomorphism
         * checking algorithm as depending on the head node, the result will be different */
        return ObjectPrinter.print(this).equals(ObjectPrinter.print(that));
    }

//    protected class ObjectWrappedPlaceholder implements WrappedPlaceholder {
//        final int fieldIndex;
//
//        public ObjectWrappedPlaceholder(final int fieldIndex) {
//            this.fieldIndex = fieldIndex;
//        }
//
//        @Override
//        public void substitute(Wrapped wrapped) {
//            WrappedObject.this.values[this.fieldIndex] = wrapped;
//        }
//    }
//
//    protected static class UnwrappedObjectPlaceholder implements UnwrappedPlaceholder {
//        /* this is the object whose field is going to be replaced by some unwrapped object */
//        final Object sourceObject;
//
//        final Field field;
//
//        public UnwrappedObjectPlaceholder(final Object sourceObject, final Field field) {
//            this.sourceObject = sourceObject;
//            this.field = field;
//        }
//
//        @Override
//        public void substitute(final Object unwrappedTargetObject) throws Exception {
//            FieldUtils.writeField(this.field, this.sourceObject, unwrappedTargetObject, true);
//        }
//    }
}
