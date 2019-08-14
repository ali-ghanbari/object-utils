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

import java.util.Objects;

public class W {
    private final Object core;

    private W(Object core) {
        this.core = core;
    }

    public static W of(final Object core) {
        return new W(core);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this.core == o) {
//            return true;
//        }
//        if (o == null) {
//            return false;
//        }
//        if (this.core.getClass() != o.getClass()) {
//            return false;
//        }
//        return Objects.equals(this.core, o);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(this.core);
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        W w = (W) o;
        return this.core == w.core;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.core);
    }
}
