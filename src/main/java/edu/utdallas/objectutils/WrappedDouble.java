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

/**
 * A wrapped <code>double</code> value which is <code>Serializable</code>,
 * and also implements <code>hashCode</code> and <code>equals</code> methods appropriately.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class WrappedDouble implements Wrapped {
    private static final long serialVersionUID = 1L;

    protected final double value;

    public WrappedDouble(double value) {
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
        WrappedDouble that = (WrappedDouble) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return ((Double) this.value).hashCode();
    }

    @Override
    public String print() {
        return String.valueOf(this.value);
    }

    @Override
    public Double unwrap() {
        return this.value;
    }

    @Override
    public Double unwrap(Object template) throws Exception {
        return this.value;
    }

    @Override
    public int getAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double distance(final Wrapped wrapped) {
        if (wrapped instanceof WrappedDouble) {
            return Double.compare(this.value, ((WrappedDouble) wrapped).value) == 0 ? 0D : 1D;
        }
        return Double.POSITIVE_INFINITY;
    }
}
