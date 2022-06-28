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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveWrapper;

public final class ObjectUtils {
    private final Predicate<Field> included;

    private final int maxDepth;

    private final int maxInheritanceDepth;

    private final Set<W<Proxy>> lhsVisited;

    private final Set<W<Proxy>> rhsVisited;

    private final Map<W<?>, Proxy> proxyCache;

    private ObjectUtils(final Predicate<Field> included,
                        final int maxDepth,
                        final int maxInheritanceDepth) {
        this.included = included;
        this.maxDepth = maxDepth;
        this.maxInheritanceDepth = maxInheritanceDepth;
        this.lhsVisited = new HashSet<>();
        this.rhsVisited = new HashSet<>();
        this.proxyCache = new HashMap<>();
    }

    public static ObjectUtils build() {
        return new ObjectUtils(m -> true, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public ObjectUtils include(final Predicate<Field> included) {
        return new ObjectUtils(included, this.maxDepth, this.maxInheritanceDepth);
    }

    public ObjectUtils withMaxDepth(final int maxDepth) {
        return new ObjectUtils(this.included, maxDepth, this.maxInheritanceDepth);
    }

    public ObjectUtils withMaxInheritanceDepth(final int maxInheritanceDepth) {
        return new ObjectUtils(this.included, this.maxDepth, maxInheritanceDepth);
    }

    public boolean deepEquals(final Object lhs, final Object rhs) {
        if (!(lhs instanceof Proxy) || !(rhs instanceof Proxy)) {
            return false;
        }
        if (lhs == rhs) {
            return true;
        }
        this.lhsVisited.clear();
        this.rhsVisited.clear();
        return deepEquals0(new W<>((Proxy) lhs), new W<>((Proxy) rhs));
    }

    public boolean deepEquals0(final W<Proxy> lhsW, final W<Proxy> rhsW) {
        final Proxy lhs = lhsW.value;
        final Proxy rhs = rhsW.value;

        if (lhs == rhs) {
            return true;
        }

        if (lhs.getClass() != rhs.getClass()) {
            return false;
        }

        if (lhs instanceof TerminalProxy) {
            return lhs.equals(rhs);
        }

        if (this.lhsVisited.contains(lhsW)) {
            return this.rhsVisited.contains(rhsW);
        } else {
            if (this.rhsVisited.contains(rhsW)) {
                return false;
            }
        }

        this.lhsVisited.add(lhsW);
        this.rhsVisited.add(rhsW);

        if (lhs instanceof AbstractCompositeObjectProxy) {
            final AbstractCompositeObjectProxy lhsObj = (AbstractCompositeObjectProxy) lhs;
            final AbstractCompositeObjectProxy rhsObj = (AbstractCompositeObjectProxy) rhs;
            if (lhsObj.typeName.equals(rhsObj.typeName)) {
                final int size = lhsObj.values.size();
                if (size == rhsObj.values.size()) {
                    for (int i = 0; i < size; i++) {
                        if (!deepEquals0(new W<>(lhsObj.values.get(i)), new W<>(rhsObj.values.get(i)))) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public Object makeSerializable(final Object object) {
        this.proxyCache.clear();
        try {
            return makeSerializable0(new W<>(object), 0);
        } catch (final IllegalAccessException ignored) {
            throw new InternalError("Unexpected IllegalAccessException");
        }
    }

    private Proxy makeSerializable0(final W<?> objW, final int depth) throws IllegalAccessException {
        if (depth >= this.maxDepth) {
            return Skipped.V;
        }

        final Proxy result = this.proxyCache.get(objW);

        if (result != null) {
            return result;
        }

        final Object obj = objW.value;

        if (obj == null) {
            this.proxyCache.put(objW, Null.V);
            return Null.V;
        }
        Class<?> clazz = obj.getClass();

        if (isPrimitiveOrWrapper(clazz)) {
            final Proxy proxy = new PrimitiveOrWrapperProxy(obj);
            this.proxyCache.put(objW, proxy);
            return proxy;
        }

        if (obj instanceof HashSet || obj instanceof HashMap || obj instanceof Hashtable) {
            this.proxyCache.put(objW, Skipped.V);
            return Skipped.V;
        }

        if (clazz.isArray()) {
            final Proxy proxy;
            final Class<?> componentType = clazz.getComponentType();
            if (componentType.isPrimitive()) {
                proxy = new PrimitiveOrWrapperArrayProxy(clonePrimitiveArray(obj));
                this.proxyCache.put(objW, proxy);
            } else if (isPrimitiveWrapper(componentType)) {
                proxy = new PrimitiveOrWrapperArrayProxy(clonePrimitiveWrapperArray(obj));
                this.proxyCache.put(objW, proxy);
            } else {
                final int length = Array.getLength(obj);
                final List<Proxy> values = new ArrayList<>(length);
                proxy = new ObjectArrayProxy(clazz, values);
                this.proxyCache.put(objW, proxy);
                for (int i = 0; i < length; i++) {
                    values.add(makeSerializable0(new W<>(Array.get(obj, i)), depth + 1));
                }
            }
            return proxy;
        }

        if (clazz.isEnum()) {
            final Proxy proxy = new EnumProxy((Enum<?>) obj);
            this.proxyCache.put(objW, proxy);
            return proxy;
        }

        final List<Proxy> fieldValues = new ArrayList<>();
        final Proxy proxy = new ObjectProxy(clazz, fieldValues);
        this.proxyCache.put(objW, proxy);
        int inheritanceDepth = 0;
        while (clazz != null && inheritanceDepth < this.maxInheritanceDepth) {
            final Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (final Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        || !this.included.test(field)
                        || field.getName().contains("$")) {
                    continue;
                }
                fieldValues.add(makeSerializable0(new W<>(field.get(obj)), depth + 1));
            }
            clazz = clazz.getSuperclass();
            inheritanceDepth++;
        }

        return proxy;
    }

    private static Object clonePrimitiveArray(final Object array) {
        if (array instanceof int[]) {
            return ((int[]) array).clone();
        } else if (array instanceof long[]) {
            return ((long[]) array).clone();
        } else if (array instanceof short[]) {
            return ((short[]) array).clone();
        } else if (array instanceof byte[]) {
            return ((byte[]) array).clone();
        } else if (array instanceof boolean[]) {
            return ((boolean[]) array).clone();
        } else if (array instanceof char[]) {
            return ((char[]) array).clone();
        } else if (array instanceof float[]) {
            return ((float[]) array).clone();
        } else if (array instanceof double[]) {
            return ((double[]) array).clone();
        }
        throw new IllegalArgumentException("Not a primitive-typed array");
    }

    private static Object clonePrimitiveWrapperArray(final Object array) {
        if (array instanceof Integer[]) {
            return ((Integer[]) array).clone();
        } else if (array instanceof Long[]) {
            return ((Long[]) array).clone();
        } else if (array instanceof Short[]) {
            return ((Short[]) array).clone();
        } else if (array instanceof Byte[]) {
            return ((Byte[]) array).clone();
        } else if (array instanceof Boolean[]) {
            return ((Boolean[]) array).clone();
        } else if (array instanceof Character[]) {
            return ((Character[]) array).clone();
        } else if (array instanceof Float[]) {
            return ((Float[]) array).clone();
        } else if (array instanceof Double[]) {
            return ((Double[]) array).clone();
        } else if (array instanceof Void[]) {
            return ((Void[]) array).clone();
        }
        throw new IllegalArgumentException("Not a primitive wrapper array");
    }

    public double extendedHammingDistance(final Object lhs, final Object rhs) {
        this.lhsVisited.clear();
        this.rhsVisited.clear();
        return extendedHammingDistance0(new W<>((Proxy) lhs), new W<>((Proxy) rhs));
    }

    private double extendedHammingDistance0(final W<Proxy> lhsW, final W<Proxy> rhsW) {
        final Proxy lhs = lhsW.value;
        final Proxy rhs = rhsW.value;

        if (lhs == rhs) {
            return 0D;
        }

        if (lhs.getClass() != rhs.getClass()) {
            return 1D;
        }

        if (lhs instanceof Skipped || lhs instanceof Null) {
            return 0D;
        }

        if (lhs instanceof PrimitiveOrWrapperProxy) {
            final PrimitiveOrWrapperProxy lhsObj = (PrimitiveOrWrapperProxy) lhs;
            final PrimitiveOrWrapperProxy rhsObj = (PrimitiveOrWrapperProxy) rhs;
            if (lhsObj.obj.equals(rhsObj.obj)) {
                return 0D;
            }
            return 1D;
        }

        if (lhs instanceof PrimitiveOrWrapperArrayProxy) {
            final Object lhsArray = ((PrimitiveOrWrapperArrayProxy) lhs).array;
            final Object rhsArray = ((PrimitiveOrWrapperArrayProxy) rhs).array;
            if (lhsArray.getClass().getComponentType() != rhsArray.getClass().getComponentType()) {
                return 1D;
            }
            final int length = Array.getLength(lhsArray);
            if (length != Array.getLength(rhsArray)) {
                return 1D;
            }
            double distance = 0D;
            for (int i = 0; i < length; i++) {
                if (!Objects.equals(Array.get(lhsArray, i), Array.get(rhsArray, i))) {
                    distance += 1D;
                }
            }
            return distance;
        }

        if (lhs instanceof EnumProxy) {
            final EnumProxy lhsEnum = (EnumProxy) lhs;
            final EnumProxy rhsEnum = (EnumProxy) rhs;
            if (lhsEnum.typeName.equals(rhsEnum.typeName) && lhsEnum.constName.equals(rhsEnum.constName)) {
                return 0D;
            }
            return 1D;
        }

        if (this.lhsVisited.contains(lhsW)) {
            return this.rhsVisited.contains(rhsW) ? 0D : 1D;
        } else {
            if (this.rhsVisited.contains(rhsW)) {
                return 1D;
            }
        }

        this.lhsVisited.add(lhsW);
        this.rhsVisited.add(rhsW);

        if (lhs instanceof AbstractCompositeObjectProxy) {
            final AbstractCompositeObjectProxy lhsObj = (AbstractCompositeObjectProxy) lhs;
            final AbstractCompositeObjectProxy rhsObj = (AbstractCompositeObjectProxy) rhs;
            if (!lhsObj.typeName.equals(rhsObj.typeName)) {
                return 1D;
            }
            final int size = lhsObj.values.size();
            if (size != rhsObj.values.size()) {
                return 1D;
            }
            double distance = 0D;
            for (int i = 0; i < size; i++) {
                distance += extendedHammingDistance0(new W<>(lhsObj.values.get(i)), new W<>(rhsObj.values.get(i)));
            }
            return distance;
        }

        throw new IllegalArgumentException();
    }
}