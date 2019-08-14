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