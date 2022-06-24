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

import java.util.Arrays;
import java.util.List;

class C1 extends B {
        final int f1;
        final String f2;

        private final List<B> fl;

        public C1(double f, int f1, String f2) {
            super(f);
            this.f1 = f1;
            this.f2 = f2;
            final C1[] o = new C1[1];
            o[0] = this;
            this.fl = Arrays.asList(this, null, new B(2), new C2(0.001, 7, o));
        }
    }