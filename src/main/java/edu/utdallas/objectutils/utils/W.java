package edu.utdallas.objectutils.utils;

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
 * Yet another wrapper class that allows us override the behavior of
 * already implemented <code>equals</code> method.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class W {
    private final Object core;

    private W(Object core) {
        this.core = core;
    }

    public static W of(final Object core) {
        return new W(core);
    }

    public Object getCore() {
        return this.core;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof W)) {
            return false;
        }
        final W w = (W) o;
        return this.core == w.core;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this.core);
    }
}
