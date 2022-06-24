package edu.iastate.objectutils;

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
import java.util.function.Predicate;

import static edu.iastate.objectutils.Utils.isJDKClass;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

public final class ObjectUtils {
    private final Predicate<Field> included;

    private final int maxDepth;

    private final int maxInheritanceDepth;
    private final Map<W<Proxy>, Integer> lhsVisited;

    private final Map<W<Proxy>, Integer> rhsVisited;

    private final Map<W<?>, Proxy> proxyCache;

    private final Map<W<?>, String> printerCache;

    private ObjectUtils(final Predicate<Field> included,
                        final int maxDepth,
                        final int maxInheritanceDepth) {
        this.included = included;
        this.maxDepth = maxDepth;
        this.maxInheritanceDepth = maxInheritanceDepth;
        this.lhsVisited = new HashMap<>();
        this.rhsVisited = new HashMap<>();
        this.proxyCache = new HashMap<>();
        this.printerCache = new HashMap<>();
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
        if (lhs == null && rhs == null) {
            return true;
        }
        if (!(lhs instanceof Proxy) || !(rhs instanceof Proxy)) {
            return false;
        }
        if (lhs == rhs) {
            return true;
        }
        if (this.lhsVisited.size() > 0 || this.rhsVisited.size() > 0) {
            throw new IllegalStateException();
        }
        return deepEquals0(new W<>((Proxy) lhs), new W<>((Proxy) rhs));
    }

    private void visit(final Map<W<Proxy>, Integer> registry, final W<Proxy> item) {
        registry.compute(item, (__, v) -> v == null ? 1 : (v + 1));
    }

    private boolean hasVisited(final Map<W<Proxy>, Integer> registry, final W<Proxy> item) {
        return registry.containsKey(item);
    }

    private void unvisit(final Map<W<Proxy>, Integer> registry, final W<Proxy> item) {
        registry.compute(item, (__, v) -> v == 1 ? null : (v - 1));
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

        if (hasVisited(this.lhsVisited, lhsW)) {
            return hasVisited(this.rhsVisited, rhsW);
        } else {
            if (hasVisited(this.rhsVisited, rhsW)) {
                return false;
            }
        }

        visit(this.lhsVisited, lhsW);
        visit(this.rhsVisited, rhsW);

        if (lhs instanceof NonDetIterableProxy) {
            final List<Proxy> lhsElements = ((NonDetIterableProxy) lhs).elements;
            final List<Proxy> rhsElements = ((NonDetIterableProxy) rhs).elements;
            final int size = lhsElements.size();
            if (size == rhsElements.size()) {
                for (int i = 0; i < size; i++) {
                    if (!deepEquals0(new W<>(lhsElements.get(i)), new W<>(rhsElements.get(i)))) {
                        unvisit(this.lhsVisited, lhsW);
                        unvisit(this.rhsVisited, rhsW);
                        return false;
                    }
                }
                unvisit(this.lhsVisited, lhsW);
                unvisit(this.rhsVisited, rhsW);
                return true;
            }
        } else if (lhs instanceof AbstractCompositeObjectProxy) {
            final AbstractCompositeObjectProxy lhsObj = (AbstractCompositeObjectProxy) lhs;
            final AbstractCompositeObjectProxy rhsObj = (AbstractCompositeObjectProxy) rhs;
            if (lhsObj.typeName.equals(rhsObj.typeName)) {
                final int size = lhsObj.values.size();
                if (size == rhsObj.values.size()) {
                    for (int i = 0; i < size; i++) {
                        if (!deepEquals0(new W<>(lhsObj.values.get(i)), new W<>(rhsObj.values.get(i)))) {
                            unvisit(this.lhsVisited, lhsW);
                            unvisit(this.rhsVisited, rhsW);
                            return false;
                        }
                    }
                }
            }
        }
        unvisit(this.lhsVisited, lhsW);
        unvisit(this.rhsVisited, rhsW);
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
            return EOG.V;
        }

        final Proxy result = this.proxyCache.get(objW);

        if (result != null) {
            return result;
        }

        final Object obj = objW.value;

        if (obj == null) {
            this.proxyCache.put(objW, EOG.V);
            return EOG.V;
        }
        Class<?> clazz = obj.getClass();

        if (isPrimitiveOrWrapper(clazz)) {
            final Proxy proxy = new PrimitiveOrWrapperProxy(obj);
            this.proxyCache.put(objW, proxy);
            return proxy;
        }

        if (isJDKClass(clazz)) {
            if (obj instanceof Iterable) {
                if (obj instanceof HashSet) {
                    return makeNonDetIterableProxy(objW, (Iterable<?>) obj, depth);
                }
                return makeDetIterableProxy(clazz, objW, (Iterable<?>) obj, depth);
            } else if (obj instanceof Map) {
                if (obj instanceof HashMap || obj instanceof Hashtable) {
                    return makeNonDetIterableProxy(objW, ((Map<?, ?>) obj).entrySet(), depth);
                }
                return makeDetIterableProxy(clazz, objW, ((Map<?, ?>) obj).entrySet(), depth);
            }
            this.proxyCache.put(objW, EOG.V);
            return EOG.V;
        }

        if (clazz.isArray()) {
            final Proxy proxy;
            if (isPrimitiveOrWrapper(clazz.getComponentType())) {
                proxy = new PrimitiveOrWrapperArrayProxy(cloneArray(obj));
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

    private Proxy makeNonDetIterableProxy(final W<?> objW,
                                          final Iterable<?> iterable,
                                          final int depth) throws IllegalAccessException {
        final List<Proxy> elements = new ArrayList<>();
        final NonDetIterableProxy proxy = new NonDetIterableProxy(elements);
        this.proxyCache.put(objW, proxy);
        for (final Object element : iterable) {
            elements.add(makeSerializable0(new W<>(element), depth + 1));
        }
        return proxy;
    }

    private Proxy makeDetIterableProxy(final Class<?> clazz,
                                       final W<?> objW,
                                       final Iterable<?> iterable,
                                       final int depth) throws IllegalAccessException {
        final List<Proxy> elements = new ArrayList<>();
        final ObjectProxy proxy = new ObjectProxy(clazz, elements);
        this.proxyCache.put(objW, proxy);
        for (final Object element : iterable) {
            elements.add(makeSerializable0(new W<>(element), depth + 1));
        }
        return proxy;
    }

    private static Object cloneArray(final Object array) {
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
        } else if (array instanceof Integer[]) {
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
        }
        return ((Void[]) array).clone();
    }

    private String print0(final W<Proxy> proxyW) {
        final String result = this.printerCache.get(proxyW);

        if (result != null) {
            return result;
        }

        visit(this.lhsVisited, lhsW);
        visit(this.rhsVisited, rhsW);

        if (lhs instanceof NonDetIterableProxy) {
            final List<Proxy> lhsElements = ((NonDetIterableProxy) lhs).elements;
            final List<Proxy> rhsElements = ((NonDetIterableProxy) rhs).elements;
            final int size = lhsElements.size();
            if (size == rhsElements.size()) {
                for (int i = 0; i < size; i++) {
                    if (!deepEquals0(new W<>(lhsElements.get(i)), new W<>(rhsElements.get(i)))) {
                        unvisit(this.lhsVisited, lhsW);
                        unvisit(this.rhsVisited, rhsW);
                        return false;
                    }
                }
                unvisit(this.lhsVisited, lhsW);
                unvisit(this.rhsVisited, rhsW);
                return true;
            }
        } else if (lhs instanceof AbstractCompositeObjectProxy) {
            final AbstractCompositeObjectProxy lhsObj = (AbstractCompositeObjectProxy) lhs;
            final AbstractCompositeObjectProxy rhsObj = (AbstractCompositeObjectProxy) rhs;
            if (lhsObj.typeName.equals(rhsObj.typeName)) {
                final int size = lhsObj.values.size();
                if (size == rhsObj.values.size()) {
                    for (int i = 0; i < size; i++) {
                        if (!deepEquals0(new W<>(lhsObj.values.get(i)), new W<>(rhsObj.values.get(i)))) {
                            unvisit(this.lhsVisited, lhsW);
                            unvisit(this.rhsVisited, rhsW);
                            return false;
                        }
                    }
                }
            }
        }
        unvisit(this.lhsVisited, lhsW);
        unvisit(this.rhsVisited, rhsW);
        return false;
    }
}