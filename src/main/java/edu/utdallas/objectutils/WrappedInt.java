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

/**
 * A wrapped <code>int</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public class WrappedInt implements Wrapped {
    private static final long serialVersionUID = 1L;

    protected final int value;

    public WrappedInt(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrappedInt that = (WrappedInt) o;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean coreEquals(Object core) {
        return core instanceof Integer && (this.value == (Integer) core);
    }

    @Override
    public String print() {
        return String.valueOf(this.value);
    }

    @Override
    public Integer unwrap() {
        return this.value;
    }

    @Override
    public Integer unwrap(final ModificationPredicate shouldMutate) {
        return this.value;
    }

    @Override
    public Integer unwrap(Object template) throws Exception {
        return this.value;
    }

    @Override
    public Integer unwrap(Object template, ModificationPredicate shouldMutate) throws Exception {
        return this.value;
    }

    @Override
    public int getAddress() {
        throw new UnsupportedOperationException();
    }
}
