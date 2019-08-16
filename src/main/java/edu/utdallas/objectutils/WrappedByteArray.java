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

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class WrappedByteArray implements WrappedArray {
    private static final long serialVersionUID = 1L;

    private final byte[] value;

    public WrappedByteArray(byte[] value) {
        this.value = value;
    }

    public WrappedByteArray(Byte[] value) {
        this.value = ArrayUtils.toPrimitive(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedByteArray that = (WrappedByteArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.value);
    }

    @Override
    public byte[] unwrap() {
        return this.value;
    }

    @Override
    public byte[] unwrap(ModificationPredicate shouldMutate) {
        return this.value;
    }

    @Override
    public int getAddress() {
        throw new UnsupportedOperationException();
    }
}
