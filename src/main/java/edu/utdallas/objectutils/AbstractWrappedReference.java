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

import static edu.utdallas.objectutils.Commons.newAddress;

/**
 * Base class for all non-primitive objects: arrays and proper objects
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public abstract class AbstractWrappedReference implements Wrapped {
    protected final int address;

    protected AbstractWrappedReference() {
        this.address = newAddress();
    }

    @Override
    public int getAddress() {
        return this.address;
    }

    /**
     * A method useful for getting the name of the underlying type
     * when the wrapped class is not available in the classpath.
     *
     * @return The name of the underlying wrapped type.
     * @since 1.1
     */
    public abstract String getTypeName();
}
