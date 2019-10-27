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

public class WrappedPrimitiveCharArray extends AbstractWrappedBasicArray<char[]> {
    public WrappedPrimitiveCharArray(char[] value) {
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
        WrappedPrimitiveCharArray that = (WrappedPrimitiveCharArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public boolean coreEquals(Object core) {
        return core instanceof char[] && Arrays.equals((char[]) core, this.value);
    }

    @Override
    public String print() {
        return Arrays.toString(this.value);
    }

    @Override
    public char[] unwrap() {
        return this.value.clone();
    }

    @Override
    public char[] unwrap(ModificationPredicate shouldMutate) {
        return this.value.clone();
    }

    @Override
    public char[] unwrap(Object template) throws Exception {
        System.arraycopy(this.value, 0, template, 0, this.value.length);
        return (char[]) template;
    }

    @Override
    public char[] unwrap(Object template, ModificationPredicate shouldMutate) throws Exception {
        System.arraycopy(this.value, 0, template, 0, this.value.length);
        return (char[]) template;
    }
}