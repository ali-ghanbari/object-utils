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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

/**
 * Wraps an arbitrary object by recursively storing all of its field values.
 * This class is is <code>Serializable</code> and also implements <code>hashCode</code>
 * and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class WrappedObject extends AbstractWrappedCompositeObject {
    private static final long serialVersionUID = 1L;

    private static final Pattern PATTERN = Pattern.compile("name|ordinal");

    protected transient Iterator<Field> fieldsIterator;

    protected transient Field fieldAtCursor;

    public WrappedObject(Class<?> type, Wrapped[] values) {
        super(type, values);
    }

    @Override
    protected Object rectifyTemplate(final Object template) {
        if (template == null) {
            return createRawObject();
        }
        if (this.type.retrieveClass() != template.getClass()) {
            return null;
        }
        return template;
    }

    @Override
    protected Object createRawObject() {
        return ObjenesisHelper.newInstance(this.type.retrieveClass());
    }

    @Override
    protected void resetCursor() {
        this.fieldsIterator = getAllFieldsList(this.type.retrieveClass()).iterator();
    }

    @Override
    protected void advanceCursor() {
        this.fieldAtCursor = this.fieldsIterator.next();
    }

    @Override
    protected boolean skippedAtCursor() {
        if (Modifier.isStatic(this.fieldAtCursor.getModifiers())) {
            return true;
        }
        return this.fieldAtCursor.getDeclaringClass() == Enum.class
                && PATTERN.matcher(this.fieldAtCursor.getName()).matches();
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
}
