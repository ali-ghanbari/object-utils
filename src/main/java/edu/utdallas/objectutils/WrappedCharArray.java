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

public class WrappedCharArray extends AbstractWrappedBasicArray<Character[]> {
    public WrappedCharArray(Character[] value) {
        super(value.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrappedCharArray)) {
            return false;
        }
        WrappedCharArray that = (WrappedCharArray) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

//    @Override
//    public boolean coreEquals(Object core) {
//        return core instanceof Character[] && Arrays.equals((Character[]) core, this.value);
//    }

    @Override
    public Character[] unwrap() throws Exception {
        return this.value.clone();
    }

    @Override
    public Character[] unwrap(ModificationPredicate shouldMutate) throws Exception {
        return this.value.clone();
    }

    @Override
    public Character[] unwrap(Object template) throws Exception {
        System.arraycopy(this.value, 0, template, 0, this.value.length);
        return (Character[]) template;
    }

    @Override
    public Character[] unwrap(Object template, ModificationPredicate shouldMutate) throws Exception {
        System.arraycopy(this.value, 0, template, 0, this.value.length);
        return (Character[]) template;
    }

    @Override
    public String print() {
        return Arrays.toString(this.value);
    }
}
