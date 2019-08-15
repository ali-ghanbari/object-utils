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

import java.lang.reflect.Field;

interface WrappedObjectPlaceholder {
    void substitute(final Wrapped wrappedObject);
}

class UnwrappedObjectPlaceholder {
    /* this is the object whose field is going to be replaced by some unwrapped object */
    final Object sourceObject;

    final Field field;

    public UnwrappedObjectPlaceholder(final Object sourceObject, final Field field) {
        this.sourceObject = sourceObject;
        this.field = field;
    }

    public void substitute(final Object reifiedTargetObject) throws Exception {
        FieldUtils.writeField(this.field, this.sourceObject, reifiedTargetObject, true);
    }
}