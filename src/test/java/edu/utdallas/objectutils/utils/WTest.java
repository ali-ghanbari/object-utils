package edu.utdallas.objectutils.utils;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class WTest {
//    @Test
//    public void test1() {
//        final Set<W> set = new HashSet<>();
//        final W w1 = W.of("hello");
//        final W w2 = W.of("world");
//        final W w3 = W.of("how");
//        final W w4 = W.of("are you?");
//        set.add(w1);
//        set.add(w2);
//        set.add(w3);
//        set.add(w4);
//        assertEquals(w1, "hello");
////        assertTrue(set.contains("hello"));
////        assertTrue(set.contains("world"));
////        assertTrue(set.contains("how"));
////        assertTrue(set.contains("are you?"));
////        assertFalse(set.contains("hey!"));
//    }

    @Test
    public void test1() {
        final Set<W> set = new HashSet<>();
        final W w1 = W.of("hello");
        final W w2 = W.of("world");
        final W w3 = W.of("how");
        final W w4 = W.of("are you?");
        set.add(w1);
        set.add(w2);
        set.add(w3);
        set.add(w4);
        assertTrue(set.contains(W.of("hello")));
        assertTrue(set.contains(W.of("world")));
        assertTrue(set.contains(W.of("how")));
        assertTrue(set.contains(W.of("are you?")));
        assertFalse(set.contains(W.of("hey!")));
    }
}