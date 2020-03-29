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

import static edu.utdallas.objectutils.Commons.wrappedDistance;

/**
 * Represents a null reference
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public enum WrappedNull implements Wrapped {
    INSTANCE;

    @Override
    public <T> T unwrap() {
        return null;
    }

    @Override
    public <T> T unwrap(Object template) {
        return null;
    }

    @Override
    public int getAddress() {
        return 0;
    }

    @Override
    public String print() {
        return "null";
    }

    @Override
    public double distance(final Wrapped wrapped) {
        return wrappedDistance(this, wrapped);
    }
}
