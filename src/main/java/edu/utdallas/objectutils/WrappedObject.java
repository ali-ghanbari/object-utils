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
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;
import static edu.utdallas.objectutils.Commons.getAllFieldsList;
import static edu.utdallas.objectutils.Commons.writeField;
import static edu.utdallas.objectutils.Commons.readField;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedObject extends AbstractWrappedCompositeObject {
    private static final long serialVersionUID = 1L;

    protected transient Iterator<Field> fieldsIterator;

    protected transient Field fieldAtCursor;

    public WrappedObject(Class<?> type, Wrapped[] values) {
        super(type, values);
    }

    @Override
    protected Object createRawObject() {
        return ObjenesisHelper.newInstance(this.type);
    }

    @Override
    protected void resetCursor() {
        this.fieldsIterator = getAllFieldsList(this.type).iterator();
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
        writeField(this.fieldAtCursor, rawObject, value, true);
    }

    @Override
    protected Object getAtCursor(Object rawObject) throws Exception {
        return readField(this.fieldAtCursor, rawObject, true);
    }

    @Override
    public String print() {
        return ObjectPrinter.print(this);
    }

    @Override
    protected boolean coreTypeCheck(Object core) {
        return core != null && core.getClass() == this.type;
    }
}
