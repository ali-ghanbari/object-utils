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

import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;

/**
 * Utility functions common to all modules
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
final class Commons {
    private static final EditDistance<Integer> LEVEN_DIST;

    private static int addressCounter;

    static {
        addressCounter = 0;
        LEVEN_DIST = new LevenshteinDistance();
    }

    private Commons() {

    }

    static int newAddress() {
        return addressCounter++;
    }

    static void resetAddressCounter() {
        addressCounter = 0;
    }

    /**
     * Calculates distance between two wrapped objects
     *
     * @param wrapped1 First wrapped object
     * @param wrapped2 Second wrapped object
     * @return Distance between <code>wrapped1</code> and <code>wrapped2</code>
     */
    static double wrappedDistance(final Wrapped wrapped1, final Wrapped wrapped2) {
        if (wrapped1.equals(wrapped2)) {
            return 0D;
        }
        final int cost1 = getCost(wrapped1);
        final int cost2 = getCost(wrapped2);
        return wrappedDistance(wrapped1, cost1, wrapped2, cost2, new HashSet<Integer>());
    }

    // only for debugging purposes
    static double arrayDistance(final Object left, final Object right) {
        return arrayDistance0(left, right, new HashSet<Integer>());
    }

    private static double arrayDistance0(Object left,
                                         Object right,
                                         final Set<Integer> visited) {
        if (left == right) {
            return 0D;
        }
        if (left == null || right == null) {
            throw new IllegalArgumentException("Both should be null or non-null");
        }
        final Class<?> type = left.getClass();
        if (type != right.getClass()) {
            throw new IllegalArgumentException("The input should both be of the same type");
        }
        final Class<?> componentType = type.getComponentType();
        if (componentType == null) {
            throw new IllegalArgumentException("Args must be arrays");
        }
        int n = Array.getLength(left); // length of left
        int m = Array.getLength(right); // length of right

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input arrays to consume less memory
            final Object tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = Array.getLength(right);
        }

        double[] p = new double[n + 1];

        // indexes into arrays left and right
        int i; // iterates through left
        int j; // iterates through right
        double upperLeft;
        double upper;

        Object rightJ; // jth object of right
        double replacementCost; // cost of replacement

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = Array.get(right, j - 1);
            p[0] = j;

            final int rightJCost;
            if (rightJ instanceof Wrapped) {
                rightJCost = getCost((Wrapped) rightJ);
            } else {
                rightJCost = 1;
            }

            for (i = 1; i <= n; i++) {
                upper = p[i];
                Object leftI = Array.get(left, i - 1);

                final int leftICost;
                if (leftI instanceof Wrapped) {
                    leftICost = getCost((Wrapped) leftI);
                } else {
                    leftICost = 1;
                }

                replacementCost = getCost(leftI, leftICost, rightJ, rightJCost, visited);

                // minimum of cell to the left+cost, to the top+cost, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + leftICost, p[i] + leftICost), upperLeft + replacementCost);
                upperLeft = upper;
            }
        }

        return p[n];
    }

    private static <T> double getCost(final T left,
                                      final int leftCost,
                                      final T right,
                                      final int rightCost,
                                      final Set<Integer> visited) {
        if (left == right) {
            return 0D;
        }
        if (left == null || right == null || left.getClass() != right.getClass()) {
            return leftCost + rightCost;
        }
        if (left instanceof Wrapped) {
            return wrappedDistance((Wrapped) left, leftCost, (Wrapped) right, rightCost, visited);
        } else if (left instanceof Number) {
            return numberDistance((Number) left, (Number) right);
        } else if (left instanceof Character) {
            return charDistance((Character) left, (Character) right);
        } else if (left instanceof Boolean) {
            return booleanDistance((Boolean) left, (Boolean) right);
        } else if (left instanceof String) {
            return LEVEN_DIST.apply((String) left, (String) right);
        } else {
            throw new IllegalArgumentException("Unknown type: " + left.getClass().getName());
        }
    }

    /**
     * Calculates the cost of creation/deletion of <code>wrapped</code>
     * The cost of creation/deletion is defined to be the number
     * of steps it takes to initialize or do garbage collection on
     * the object represented by <code>wrapped</code>. The cost is
     * directly proportional to the number of objects reachable from
     * the object represented by <code>wrapped</code>.
     *
     * @param wrapped the wrapped object
     * @return the cost of creation/deletion
     */
    static int getCost(final Wrapped wrapped) {
        try {
            return getCost0(wrapped, new HashSet<Integer>());
        } catch (Exception e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    private static int getCost0(final Wrapped wrapped,
                                final Set<Integer> visited) throws Exception {
        int cost = 1;
        if (wrapped instanceof AbstractWrappedReference) {
            final int address = ((AbstractWrappedReference) wrapped).address;
            // if an object is visited for the second time, since we have not spent
            // any time on creating the object, the cost should be 1. this is the
            // cost of doing the reference assignment.
            if (visited.contains(address)) {
                return cost;
            }
            visited.add(address);
            if (wrapped instanceof AbstractWrappedCompositeObject) {
                final AbstractWrappedCompositeObject compositeObject =
                        (AbstractWrappedCompositeObject) wrapped;
                for (final Wrapped value : compositeObject.values) {
                    cost += getCost0(value, visited);
                }
            } else { // basic array
                cost += ((WrappedArray) wrapped).size();
            }
        }
        return cost;
    }

    private static double wrappedDistance(final Wrapped wrapped1,
                                          final int wrapped1Cost,
                                          final Wrapped wrapped2,
                                          final int wrapped2Cost,
                                          final Set<Integer> visited) {
        if (wrapped1 == wrapped2) { // this takes care of null constants
            return 0D;
        }
        if (wrapped1.getClass() != wrapped2.getClass()) {
            // this is the cost of deleting one and constructing the other.
            // this cost is expected to be large.
            return wrapped1Cost + wrapped2Cost;
        }
        if (wrapped1 instanceof AbstractWrappedReference) {
            // assert wrapped2 instanceof AbstractWrappedReference
            if (wrapped1 instanceof AbstractWrappedCompositeObject) {
                final AbstractWrappedCompositeObject compositeObject1 =
                        ((AbstractWrappedCompositeObject) wrapped1);
                final AbstractWrappedCompositeObject compositeObject2 =
                        ((AbstractWrappedCompositeObject) wrapped2);
                final int addr1 = compositeObject1.address;
                final int addr2 = compositeObject2.address;
                if (visited.contains(addr1) || visited.contains(addr2)) {
                    return wrapped1Cost + wrapped2Cost;
                }
                visited.add(addr1);
                visited.add(addr2);
                final Wrapped[] values1 = compositeObject1.values;
                final Wrapped[] values2 = compositeObject2.values;
                if (wrapped1 instanceof WrappedArray) {
                    return arrayDistance0(values1, values2, visited);
                } else {
                    if (!compositeObject1.type.equals(compositeObject2.type)) {
                        return wrapped1Cost + wrapped2Cost;
                    }
                    // assert values1.length == values2.length
                    double d = 0D;
                    final int len = values1.length;
                    for (int i = 0; i < len; i++) {
                        final Wrapped value1 = values1[i];
                        final Wrapped value2 = values2[i];
                        d += wrappedDistance(value1, getCost(value1), value2, getCost(value2), visited);
                    }
                    return d;
                }
            } else { // basic array
                final Object values1 = ((AbstractWrappedBasicArray<?>) wrapped1).value;
                final Object values2 = ((AbstractWrappedBasicArray<?>) wrapped2).value;
                return arrayDistance0(values1, values2, visited);
            }
        } else {
            // null has already been addressed
            if (wrapped1 instanceof WrappedClassConstant) {
                return wrapped1.equals(wrapped2) ? 0 : (wrapped1Cost + wrapped2Cost);
            }
            final Object w1Value;
            final Object w2Value;
            try {
                final Field valueField = wrapped1.getClass().getDeclaredField("value");
                w1Value = readField(valueField, wrapped1, true);
                w2Value = readField(valueField, wrapped2, true);
            } catch (Exception e) {
                throw new IllegalStateException(e.getCause());
            }
            if (w1Value instanceof String) {
                return LEVEN_DIST.apply(((String) w1Value), ((String) w2Value));
            } else if (w1Value instanceof Character) {
                return charDistance(((Character) w1Value), ((Character) w2Value));
            } else if (w1Value instanceof Boolean) {
                return booleanDistance(((Boolean) w1Value), ((Boolean) w2Value));
            } else if (w1Value instanceof Number) {
                return numberDistance(((Number) w1Value), ((Number) w2Value));
            }
            throw new IllegalArgumentException("unreachable");
        }
    }

    private static <T extends Number> double numberDistance(final T left, final T right) {
        final double l = left.doubleValue();
        final double r = right.doubleValue();
        if (Double.isInfinite(l) || Double.isInfinite(r)) {
            return Double.POSITIVE_INFINITY;
        }
        if (Double.isNaN(l) || Double.isNaN(r)) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.abs(l - r);
    }

    private static double charDistance(final Character left, final Character right) {
        return Math.abs(((int) left) - ((int) right));
    }

    private static double booleanDistance(final Boolean left, final Boolean right) {
        return left.booleanValue() == right.booleanValue() ? 0D : 1D;
    }
}
