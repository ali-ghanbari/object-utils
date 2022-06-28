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

class EnumProxy implements TerminalProxy {
    private static final long serialVersionUID = 1L;

    final String typeName;

    final String constName;

    public EnumProxy(final Enum<?> val) {
        this.typeName = val.getClass().getName();
        this.constName = val.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnumProxy)) {
            return false;
        }
        final EnumProxy that = (EnumProxy) o;
        return this.typeName.equals(that.typeName)
                && this.constName.equals(that.constName);
    }
}
