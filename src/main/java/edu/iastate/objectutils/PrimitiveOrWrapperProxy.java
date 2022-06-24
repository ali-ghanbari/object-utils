package edu.iastate.objectutils;

/*
 * #%L
 * Object Utilities
 * %%
 * Copyright (C) 2019 - 2022 Iowa State University
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

class PrimitiveOrWrapperProxy implements TerminalProxy {
    private static final long serialVersionUID = 1L;

    final Object obj;

    public PrimitiveOrWrapperProxy(final Object obj) {
        this.obj = obj;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrimitiveOrWrapperProxy)) {
            return false;
        }
        final PrimitiveOrWrapperProxy that = (PrimitiveOrWrapperProxy) o;
        return this.obj.equals(that.obj);
    }

//    @Override
//    public String print() {
//        return obj.toString();
//    }
}
