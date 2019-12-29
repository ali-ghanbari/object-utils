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

/**
 * A wrapped <code>byte[]</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class WrappedPrimitiveByteArray extends AbstractWrappedBasicArray<byte[]> {
    public WrappedPrimitiveByteArray(byte[] value) {
        super(value.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedPrimitiveByteArray that = (WrappedPrimitiveByteArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public String print() {
        return Arrays.toString(this.value);
    }

    @Override
    public byte[] unwrap() {
        return this.value.clone();
    }

    @Override
    public byte[] unwrap(Object template) throws Exception {
        if (template == null) {
            return this.value.clone();
        }
        final byte[] dest = (byte[]) template;
        if (dest.length != this.value.length) {
            return this.value.clone();
        }
        System.arraycopy(this.value, 0, dest, 0, this.value.length);
        return dest;
    }
}