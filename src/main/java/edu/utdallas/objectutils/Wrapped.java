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

import java.io.Serializable;

/**
 * Represents a wrapped object which is <code>Serializable</code> and implements
 * <code>equals</code> and <code>hashCode</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public interface Wrapped extends Serializable {
    /**
     * Reifies wrapped object without altering any static fields.
     * <b>Note 1:</b> <code>static final</code> fields are ignored anyway.
     * <b>Note 2:</b> those fields that are not included in the wrapped object shall be left with their default values.
     *
     * @param <T> Type to be instantiated (JDK 1.8+ can infer this type).
     * @return Reified object
     * @throws Exception Any Java reflection-related exception
     */
    <T> T unwrap() throws Exception;

    <T> T unwrap(ModificationPredicate shouldMutate) throws Exception;

    /**
     * Reifies a wrapped object without altering any static fields.
     * <b>Note 1:</b> <code>static final</code> fields are ignored anyway.
     * <b>Note 2:</b> those fields that are excluded from the wrapped object shall be left with their current values.
     *
     * @param template The target object whose field value will be updated according to data present in the wrapped
     *                 object.
     * @param <T> Type to be instantiated (JDK 1.8+ can infer this type).
     * @return <code>template</code> with its fields updated (if applicable).
     * @throws Exception Any Java reflection-related exception
     */
    <T> T unwrap(Object template) throws Exception;

    <T> T unwrap(Object template, ModificationPredicate shouldMutate) throws Exception;

    int getAddress();

    String print();
}
