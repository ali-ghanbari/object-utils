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

import static edu.utdallas.objectutils.Commons.arrayDistance;

/**
 * A wrapped <code>String</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> method appropriately.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class WrappedString implements Wrapped {
    private static final long serialVersionUID = 1L;

    protected final String value;

    public WrappedString(String value) {
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
        WrappedString that = (WrappedString) o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String print() {
        return this.value;
    }

    @Override
    public String unwrap() {
        return this.value;
    }

    @Override
    public String unwrap(Object template) throws Exception {
        return this.value;
    }

    @Override
    public int getAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double distance(final Wrapped wrapped) {
        if (wrapped instanceof WrappedString) {
            return arrayDistance(this.value.toCharArray(),
                    ((WrappedString) wrapped).value.toCharArray());
        }
        return Double.POSITIVE_INFINITY;
    }
}
