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
import java.util.Objects;

/**
 * Utility functions common to all modules
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
final class Commons {
    private static int addressCounter = 0;

    private Commons() {

    }

    static int newAddress() {
        return addressCounter++;
    }

    static void resetAddressCounter() {
        addressCounter = 0;
    }

    // credit: this method is adapted from Apache Commons Text
    static double arrayDistance(Object left, Object right) {
        if (left == right) {
            return 0D;
        }
        if (left == null || right == null) {
            throw new IllegalArgumentException("If left != right ==> both should be non-null");
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
        double cost; // cost of replacement

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = Array.get(right, j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                Object leftI = Array.get(left, i - 1);

                cost = Objects.equals(leftI, rightJ) ? 0D : 1D;

                // minimum of cell to the left+cost, to the top+cost, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }

        return p[n];
    }
}
