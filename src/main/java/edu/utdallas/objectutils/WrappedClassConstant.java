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

import edu.utdallas.objectutils.utils.OnDemandClass;

/**
 * A wrapped <code>Class</code> constant whose hash code does not depend on JVM session.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class WrappedClassConstant implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final OnDemandClass value;

    public WrappedClassConstant(Class<?> value) {
        this.value = OnDemandClass.of(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrappedClassConstant)) {
            return false;
        }
        WrappedClassConstant that = (WrappedClassConstant) o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public Class<?> unwrap() throws Exception {
        return this.value.retrieveClass();
    }

    @Override
    public Class<?> unwrap(Object template) throws Exception {
        return this.value.retrieveClass();
    }

    @Override
    public int getAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String print() {
        return String.format("Class<%s>", this.value.getName());
    }
}