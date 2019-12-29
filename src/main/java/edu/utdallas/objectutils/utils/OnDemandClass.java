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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class helps load class lazily, whenever they are needed.
 * Class constants should be handled differently from default implementation,
 * as during deserialization, we might not have the wrapped classes in the
 * classpath.
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class OnDemandClass implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Map<String, Class<?>> PRIMITIVE_CLASSES;

    private final String name;

    private transient Class<?> clazz;

    static {
        PRIMITIVE_CLASSES = new HashMap<>();
        PRIMITIVE_CLASSES.put("byte", byte.class);
        PRIMITIVE_CLASSES.put("char", char.class);
        PRIMITIVE_CLASSES.put("short", short.class);
        PRIMITIVE_CLASSES.put("boolean", boolean.class);
        PRIMITIVE_CLASSES.put("int", int.class);
        PRIMITIVE_CLASSES.put("float", float.class);
        PRIMITIVE_CLASSES.put("long", long.class);
        PRIMITIVE_CLASSES.put("double", double.class);
        PRIMITIVE_CLASSES.put("void", void.class);
    }

    private OnDemandClass(final Class<?> clazz) {
        this.clazz = clazz;
        this.name = clazz.getName();
    }

    public static OnDemandClass of(final Class<?> clazz) {
        return new OnDemandClass(clazz);
    }

    public Class<?> retrieveClass() {
        try {
            if (this.clazz == null) {
                this.clazz = PRIMITIVE_CLASSES.get(this.name);
                if (this.clazz == null) {
                    this.clazz = Class.forName(this.name);
                }
            }
            return this.clazz;
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OnDemandClass)) {
            return false;
        }
        OnDemandClass that = (OnDemandClass) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
