package edu.utdallas.objectutils;

/*
 * #%L
 * Object Utilities
 * %%
 * Copyright (C) 2019 - 2020 The University of Texas at Dallas
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

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 * @since 1.2
 */
public class CommonsTest {
    @Test
    public void testBasicArrayDistance1() {
        final LevenshteinDistance oracle = new LevenshteinDistance();
        final String[][] pairs = {
                {"h", ""},
                {"he", "she"},
                {"", ""},
                {".", ".."}
        };
        for (final String[] pair : pairs) {
            System.out.println(pair[0] + " ? " + pair[1]);
            assertEquals((double) oracle.apply(pair[0], pair[1]),
                    Commons.arrayDistance(pair[0].toCharArray(), pair[1].toCharArray()),
                    1e-5);
        }
        assertTrue(oracle.apply("apple", "orange")
                <= Commons.arrayDistance("apple".toCharArray(), "orange".toCharArray()));
    }

    @Test
    public void testBasicArrayDistance2() {
        final LevenshteinDistance oracle = new LevenshteinDistance();
        final String[][] stringPairs = {
                {"a", ""},
                {"a", "aa"},
                {"a", "b"},
                {"aa", "ab"},
                {"aaaaa", "bb"}
        };
        final boolean[][][] boolPairs = {
                {{true}, {}},
                {{true}, {true, true}},
                {{true}, {false}},
                {{true, true}, {true, false}},
                {{true, true, true, true, true}, {false, false}}
        };
        final int len = stringPairs.length;
        for (int row = 0; row < len; row++) {
            System.out.println(stringPairs[row][0] + " ? " + stringPairs[row][1]);
            assertEquals((double) oracle.apply(stringPairs[row][0], stringPairs[row][1]),
                    Commons.arrayDistance(boolPairs[row][0], boolPairs[row][1]),
                    1e-5);
        }
    }

    @Test
    public void testGetCost1() throws Exception {
        final int[] ia1 = null;
        assertEquals(1, Commons.getCost(Wrapper.wrapObject(ia1)));
        final int[] ia2 = {1, 2};
        assertEquals(3, Commons.getCost(Wrapper.wrapObject(ia2)));
        final Object[] oa1 = {ia1, ia2, new double[0], "hello"};
        assertEquals(7, Commons.getCost(Wrapper.wrapObject(oa1)));
        final Object[] oa2 = new Object[3];
        oa2[0] = oa2;
        oa2[1] = oa1;
        assertEquals(10, Commons.getCost(Wrapper.wrapObject(oa2)));
    }

    private enum Enum1 {
        HELLO,
        WORLD
    }

    private enum Enum2 {
        HELLO ("a"),
        WORLD ("b");

        final String name;

        Enum2(String name) {
            this.name = name;
        }
    }

    @Test
    public void testGetCost2() throws Exception {
        assertEquals(1, Commons.getCost(Wrapper.wrapObject(Enum1.HELLO)));
        assertEquals(2, Commons.getCost(Wrapper.wrapObject(Enum2.HELLO)));
    }
}