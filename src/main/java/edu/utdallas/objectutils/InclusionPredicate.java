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
 * A functional interface for checking whether a field should be included in a wrapped object
 *
 * @author Ali Ghanbari
 */
public abstract class InclusionPredicate {
    public static final InclusionPredicate INCLUDE_ALL = new InclusionPredicate() {
        @Override
        public boolean test(Field field) {
            return true;
        }
    };

    public static final InclusionPredicate INCLUDE_NONE = new InclusionPredicate() {
        @Override
        public boolean test(Field field) {
            return false;
        }
    };

    /**
     * This check should return <code>true</code> if and only if <code>field</code> is to be included
     * in the wrapped representation of the object. Because of the complexities of dealing with field type
     * and declaring/inheriting class, I expect that any implementation of this method will check only the
     * type of the field: the method will return true iff the type is primitive and it is accessed
     * (read or updated) in the body of the method to be memoized. However, later, we might come up with more
     * advanced, admissive analyzes that let us include more fields.
     *
     * @param field the field that is queried for inclusion
     * @return <code>true</code> iff <code>field</code> should be included in the wrapped representation of the
     * object
     */
    public abstract boolean test(Field field);
}
