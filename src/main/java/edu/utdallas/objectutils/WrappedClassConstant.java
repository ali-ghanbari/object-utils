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

import java.util.Objects;

public class WrappedClassConstant implements Wrapped {
    private static final long serialVersionUID = 1L;

    private final Class<?> value;

    public WrappedClassConstant(Class<?> value) {
        this.value = value;
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
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public Class<?> unwrap() throws Exception {
        return this.value;
    }

    @Override
    public Class<?> unwrap(ModificationPredicate shouldMutate) throws Exception {
        return this.value;
    }

    @Override
    public Class<?> unwrap(Object template) throws Exception {
        return this.value;
    }

    @Override
    public Class<?> unwrap(Object template, ModificationPredicate shouldMutate) throws Exception {
        return this.value;
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
