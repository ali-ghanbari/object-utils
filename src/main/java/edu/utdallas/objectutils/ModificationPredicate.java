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

import java.lang.reflect.Field;

/**
 * A functional interface for checking whether a static field should be modified during unwrap process
 *
 * @author Ali Ghanbari
 */
public abstract class ModificationPredicate {
    public static final ModificationPredicate YES = new ModificationPredicate() {
        @Override
        public boolean test(final Field field) {
            return true;
        }
    };

    public static final ModificationPredicate NO = new ModificationPredicate() {
        @Override
        public boolean test(final Field field) {
            return false;
        }
    };

    public abstract boolean test(Field field);
}
