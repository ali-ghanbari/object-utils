package edu.utdallas.objectutils.utils;

/*
 * #%L
 * object-utils
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

import edu.utdallas.objectutils.Wrapper;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ObjectPrinterTest {
    @Test
    public void testPrint1() throws Exception {
        final Object[] o1 = {new int[] {1, 2, 3},
                new double[] {1.D, 2.D, 3.D},
                new String[] {"a", "b", "c"},
                null,
                "hello"
        };
        final Object[] o2 = {new int[] {1, 2, 3},
                new double[] {1.D, 2.D, 3.D},
                new String[] {"a", "b", "c"},
                null,
                "hello"
        };
        final String s1 = ObjectPrinter.print(Wrapper.wrapObject(o1));
        assertEquals(s1, ObjectPrinter.print(Wrapper.wrapObject(o2)));
    }

    @Test
    public void testPrint2() throws Exception {
        final Object[] contents = {new int[] {1, 2, 3},
                new double[] {1.D, 2.D, 3.D},
                new String[] {"a", "b", "c"},
                null,
                "hello"
        };

        final List<Object> l1 = new LinkedList<>();
        final List<Object> l2 = new LinkedList<>();

        for (final Object object : contents) {
            l1.add(object);
            l2.add(object);
        }
        final String s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        assertEquals(s1, ObjectPrinter.print(Wrapper.wrapObject(l2)));
    }

    @Test
    public void testPrint3() throws Exception {
        final Object[] contents = {new int[] {1, 2, 3},
                new double[] {1.D, 2.D, 3.D},
                new String[] {"a", "b", "c"},
                null,
                "hello"
        };

        final List<Object> l1 = new LinkedList<>();
        final List<Object> l2 = new LinkedList<>();

        for (final Object object : contents) {
            l1.add(object);
            l1.add(l2);
            l2.add(object);
            l2.add(l1);
        }
        final String s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        assertEquals(s1, ObjectPrinter.print(Wrapper.wrapObject(l2)));
    }

    @Test
    public void testPrint4() throws Exception {
        final Map<Integer, Object> m1 = new HashMap<>();
        final Map<Integer, Object> m2 = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            m1.put(i, "Hello " + i);
            m2.put(i, "Hello " + i);
        }
        final String s1 = ObjectPrinter.print(Wrapper.wrapObject(m1));
        assertEquals(s1, ObjectPrinter.print(Wrapper.wrapObject(m2)));
    }

    @Test
    public void testPrint5() throws Exception {
        final List<Integer> l1 = new LinkedList<>();
        final List<Integer> l2 = new LinkedList<>();
        l1.add(1);
        l2.add(1);
        l1.add(2);
        l2.add(2);
        l1.add(3);
        l2.add(3);
        String s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        String s2 = ObjectPrinter.print(Wrapper.wrapObject(l2));
        assertEquals((Integer) 0, LevenshteinDistance.getDefaultInstance().apply(s1, s2));
        l1.add(4);
        s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        s2 = ObjectPrinter.print(Wrapper.wrapObject(l2));
        System.out.println(LevenshteinDistance.getDefaultInstance().apply(s1, s2));
        l2.add(4);
        s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        s2 = ObjectPrinter.print(Wrapper.wrapObject(l2));
        System.out.println(LevenshteinDistance.getDefaultInstance().apply(s1, s2));
        l1.remove(0);
        s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        s2 = ObjectPrinter.print(Wrapper.wrapObject(l2));
        System.out.println(LevenshteinDistance.getDefaultInstance().apply(s1, s2));
        l1.remove(0);
        s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        s2 = ObjectPrinter.print(Wrapper.wrapObject(l2));
        System.out.println(LevenshteinDistance.getDefaultInstance().apply(s1, s2));
        l2.remove(0);
        s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        s2 = ObjectPrinter.print(Wrapper.wrapObject(l2));
        System.out.println(LevenshteinDistance.getDefaultInstance().apply(s1, s2));
        l2.remove(0);
        s1 = ObjectPrinter.print(Wrapper.wrapObject(l1));
        s2 = ObjectPrinter.print(Wrapper.wrapObject(l2));
        System.out.println(LevenshteinDistance.getDefaultInstance().apply(s1, s2));
    }
}