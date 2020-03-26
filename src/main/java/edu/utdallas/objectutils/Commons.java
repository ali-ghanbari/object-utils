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

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility functions common to all modules
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
final class Commons {
    // We should not use W
    private static final Set<Wrapped> VISITED_OBJECTS;

    private static final Object SELF_REF;

    private static int addressCounter;

    static {
        VISITED_OBJECTS = new HashSet<>();
        SELF_REF = new Object();
        addressCounter = 0;
    }

    private Commons() {

    }

    static int newAddress() {
        return addressCounter++;
    }

    static void resetAddressCounter() {
        addressCounter = 0;
    }

    static void resetDistanceCalculator() {
        VISITED_OBJECTS.clear();
    }

    static double arrayDistance(Object left, Object right) {
        if (left == right) {
            return 0D;
        }
        if (left == null || right == null) {
            return Double.POSITIVE_INFINITY;
        }
        final Class<?> type = left.getClass();
        if (type != right.getClass()) {
            return Double.POSITIVE_INFINITY;
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
            // swap the input strings to consume less memory
            final Object tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = Array.getLength(right);
        }

        double[] p = new double[n + 1];

        // indexes into strings left and right
        int i; // iterates through left
        int j; // iterates through right
        double upperLeft;
        double upper;

        Object rightJ; // jth character of right
        double cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = Array.get(right, j - 1);
            if (rightJ == right) {
                rightJ = SELF_REF;
            }
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                Object leftI = Array.get(left, i - 1);
                if (leftI == left) {
                    leftI = SELF_REF;
                }
                cost = getCost(leftI, rightJ);
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }

        return p[n];
    }

    private static <T> double getCost(final T left, final T right) {
        if (left == right) {
            return 0D;
        }
        if (left == SELF_REF || right == SELF_REF || left == null || right == null) {
            return Double.POSITIVE_INFINITY;
        }
        if (left instanceof Number) {
            return numberDistance((Number) left, (Number) right);
        } else if (left instanceof Character) {
            return charDistance((Character) left, (Character) right);
        } else if (left instanceof Boolean) {
            return booleanDistance((Boolean) left, (Boolean) right);
        } else if (left instanceof String) {
            return arrayDistance(((String) left).toCharArray(),((String) right).toCharArray());
        } else if (left instanceof Wrapped) {
            final Wrapped wrappedLeft = (Wrapped) left;
            final Wrapped wrappedRight = (Wrapped) right;
            if (wrappedLeft instanceof AbstractWrappedReference) {
                VISITED_OBJECTS.add(wrappedLeft);
                if (wrappedRight instanceof AbstractWrappedReference) {
                    if (VISITED_OBJECTS.contains(wrappedRight)) {
                        return 0D;
                    }
                }
                return ((AbstractWrappedReference) wrappedLeft).distance0(wrappedRight);
            }
            return wrappedLeft.distance(wrappedRight);
        }

        throw new IllegalArgumentException("Unsupported type: " + left.getClass().getName());
    }

    static <T extends Number> double numberDistance(final T left, final T right) {
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

    static double charDistance(final Character left, final Character right) {
        return Math.abs(((int) left) - ((int) right));
    }

    static double booleanDistance(final Boolean left, final Boolean right) {
        return left.booleanValue() == right.booleanValue() ? 0D : 1D;
    }
}
