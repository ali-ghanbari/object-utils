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

import static edu.utdallas.objectutils.Commons.strictlyImmutable;
import static edu.utdallas.objectutils.Commons.readField;
import static edu.utdallas.objectutils.Commons.writeField;
import static edu.utdallas.objectutils.Commons.getAllFieldsList;

import java.lang.reflect.Field;
import java.util.List;

public final class ObjectUtils {
	public static <T> void shallowCopy(final T dest, final T src) throws Exception {
		final Class<?> clazz = src.getClass();
		final List<Field> fields = getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = readField(field, src, true);
            writeField(field, dest, fieldValue, true);
        }
	}
}
