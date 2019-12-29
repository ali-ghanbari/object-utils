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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

/**
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
abstract class CompositeObjectIterator {
    protected final Object object;

    protected CompositeObjectIterator(Object object) {
        this.object = object;
    }

    public static CompositeObjectIterator forArray(final Object array) {
        return new ArrayIterator(array);
    }

    public static CompositeObjectIterator forObject(final Object object) {
        return new ObjectFieldIterator(object);
    }

    public abstract boolean hasNext();

    public abstract void advanceCursor();

    public abstract boolean skippedAtCursor();

    public abstract boolean includedAtCursor(final InclusionPredicate inclusionPredicate);

    public abstract void setAtCursor(Object value) throws Exception;

    public abstract Object getAtCursor() throws Exception;

    private static class ArrayIterator extends CompositeObjectIterator {
        private final int lastIndex;

        private int cursor;

        public ArrayIterator(Object array) {
            super(array);
            this.lastIndex = Array.getLength(array) - 1;
            this.cursor = -1;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.lastIndex;
        }

        @Override
        public void advanceCursor() {
            this.cursor++;
        }

        @Override
        public boolean skippedAtCursor() {
            return false;
        }

        @Override
        public boolean includedAtCursor(InclusionPredicate inclusionPredicate) {
            return true;
        }

        @Override
        public void setAtCursor(Object value) throws Exception {
            Array.set(this.object, this.cursor, value);
        }

        @Override
        public Object getAtCursor() throws Exception {
            return Array.get(this.object, this.cursor);
        }
    }

    private static class ObjectFieldIterator extends CompositeObjectIterator {
        private static final Pattern PATTERN = Pattern.compile("name|ordinal");

        private final Iterator<Field> fieldsIterator;

        private Field fieldAtCursor;

        public ObjectFieldIterator(Object object) {
            super(object);
            this.fieldsIterator = getAllFieldsList(object.getClass()).iterator();
        }

        @Override
        public boolean hasNext() {
            return this.fieldsIterator.hasNext();
        }

        @Override
        public void advanceCursor() {
            this.fieldAtCursor = this.fieldsIterator.next();
        }

        @Override
        public boolean skippedAtCursor() {
            if (Modifier.isStatic(this.fieldAtCursor.getModifiers())) {
                return true;
            }
            return this.fieldAtCursor.getDeclaringClass() == Enum.class
                    && PATTERN.matcher(this.fieldAtCursor.getName()).matches();
        }

        @Override
        public boolean includedAtCursor(InclusionPredicate inclusionPredicate) {
            return inclusionPredicate.test(this.fieldAtCursor);
        }

        @Override
        public void setAtCursor(Object value) throws Exception {
            writeField(this.fieldAtCursor, this.object, value, true);
        }

        @Override
        public Object getAtCursor() throws Exception {
            return readField(this.fieldAtCursor, this.object, true);
        }
    }
}
