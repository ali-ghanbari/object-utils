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
    <T> T reify() throws Exception;

    <T> T reify(boolean updateStaticFields) throws Exception;
}
