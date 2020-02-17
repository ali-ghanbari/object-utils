package edu.utdallas.objectutils;

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
                    Commons.basicArrayDistance(pair[0].toCharArray(), pair[1].toCharArray()),
                    1e-5);
        }
        assertTrue(oracle.apply("apple", "orange")
                <= Commons.basicArrayDistance("apple".toCharArray(), "orange".toCharArray()));
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
                    Commons.basicArrayDistance(boolPairs[row][0], boolPairs[row][1]),
                    1e-5);
        }
    }
}