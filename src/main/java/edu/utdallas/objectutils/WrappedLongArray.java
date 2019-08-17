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

import java.util.Arrays;

public class WrappedLongArray implements WrappedArray {
    private static final long serialVersionUID = 1L;

    private final Long[] value;

    public WrappedLongArray(Long[] value) {
        this.value = Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedLongArray that = (WrappedLongArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    private transient String stringValue = null;

    @Override
    public String print() {
        if (this.stringValue == null) {
            this.stringValue = Arrays.toString(this.value);
        }
        return this.stringValue;
    }

    @Override
    public Long[] unwrap() {
        return this.value;
    }

    @Override
    public Long[] unwrap(ModificationPredicate shouldMutate) {
        return this.value;
    }

    @Override
    public int getAddress() {
        throw new UnsupportedOperationException();
    }
}
