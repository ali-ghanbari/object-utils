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

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 */
public class DistanceTest {
    @Test
    public void testPrimArr1() throws Exception {
        final int[] a = {1, 2, 3};
        final int[] b = a.clone();
        assertEquals(Wrapper.wrapObject(a).distance(Wrapper.wrapObject(b)), 0, 1e-5);
    }

    @Test
    public void testPrimArr2() throws Exception {
        final double[] a = {3.14D, 2.718D, 0D};
        final double[] b = a.clone();
        assertEquals(Wrapper.wrapObject(a).distance(Wrapper.wrapObject(b)), 0, 1e-5);
    }

    @Test
    public void testPrimArr3() throws Exception {
        final Wrapped a = Wrapper.wrapObject(new int[]{1, 2, 3});
        final Wrapped b = Wrapper.wrapObject(new int[]{1, 1, 3});

        final double d_ab = a.distance(b);

        final Wrapped c = Wrapper.wrapObject(new int[]{1, 3, 3});

        final double d_ac = a.distance(c);

        final Wrapped d = Wrapper.wrapObject(new int[]{1, 5, 3});

        final double d_ad = a.distance(d);

        final Wrapped e = Wrapper.wrapObject(new int[]{1, 5, 3, 10});

        final double d_ae = a.distance(e);
        final double d_de = d.distance(e);

        assertEquals(d_ab, d_ac, 1e-5);
        assertTrue(d_ab < d_ad);
        assertTrue(d_de < d_ae);
    }

    @Test
    public void testPrimArr4() throws Exception {
        final Wrapped a = Wrapper.wrapObject(new Object[] {"he", new int[] {1, 2}, Collections.singletonList(3.14D)});
        final Wrapped b = Wrapper.wrapObject(new Object[] {"she", new int[] {1, 2}, Collections.singletonList(3.14D)});
        final double d = a.distance(b);
        System.out.println(d);
        assertTrue(d >= 1D);
    }

    @Test
    public void testPrimArr5() throws Exception {
        final Wrapped a = Wrapper.wrapObject(new Object[] {"she", new int[] {1, 2}, new Object[] {Collections.singletonList(3.14D), Arrays.asList(1, 2)}});
        final Wrapped b = Wrapper.wrapObject(new Object[] {"she", new int[] {1, 2}, new Object[] {Collections.singletonList(3.14D), Arrays.asList(1, 2)}});
        final double d = a.distance(b);
        assertEquals(0D, d, 1e-5);
    }

    @Test
    public void testPrimArr6() throws Exception {
        final Wrapped a = Wrapper.wrapObject(new Object[] {"she", new int[] {1, 2}, new Object[] {Collections.singletonList(3.14D), Arrays.asList(1, 2)}});
        final Wrapped b = Wrapper.wrapObject(new Object[] {"she", new int[] {1, 2}, new Object[] {Collections.singletonList(3.14D), Collections.singletonMap('a', 'b')}});
        assertTrue(Double.isInfinite(a.distance(b)));
    }

    @Test
    public void testPrimArr7() throws Exception {
        final Wrapped a = Wrapper.wrapObject(new Object[] {"she", new int[] {1, 2}, new Object[] {Collections.singletonList(3.14D), Collections.singletonMap('a', 'c')}});
        final Wrapped b = Wrapper.wrapObject(new Object[] {"she", new int[] {1, 2}, new Object[] {Collections.singletonList(3.14D), Collections.singletonMap('a', 'b')}});
        final double d = a.distance(b);
        System.out.println(d);
        assertTrue(d >= 1D);
    }

    @Test
    public void testStringArr1() throws Exception {
        final Wrapped a = Wrapper.wrapObject(new String[] {"he", "she"});
        final Wrapped b = Wrapper.wrapObject(new String[] {"she", "he"});
        System.out.println(a.distance(b));
    }

    private static class Person {
        private final int id;
        private final Date dob;
        private final String name;
        private final String address;
        private final int[] children_ids;

        public Person(int id, Date dob, String name, String address, int[] children_ids) {
            this.id = id;
            this.dob = dob;
            this.name = name;
            this.address = address;
            this.children_ids = children_ids;
        }
    }

    @Test
    public void testObject1() throws Exception {
        final long currentTime = System.currentTimeMillis();
        Wrapped a = Wrapper.wrapObject(new Person(10, new Date(currentTime), "a", "addr1", new int[] {0, 1, 2}));
        Wrapped a_prime = Wrapper.wrapObject(new Person(10, new Date(currentTime), "a", "addr1", new int[] {0, 1, 2}));
        Wrapped b = Wrapper.wrapObject(new Person(17, new Date(), "b", "addr2", new int[] {1, 11, 12, 14}));
        Wrapped c = Wrapper.wrapObject(new Person(18, new Date(), "a", "addr1", new int[] {1, 11, 12, 14}));
        assertFalse(Double.isInfinite(a.distance(a_prime)));
        assertEquals(0D, a.distance(a_prime), 1e-5D);
        assertFalse(Double.isInfinite(a.distance(b)));
        assertFalse(Double.isInfinite(a.distance(c)));
        assertTrue(a.distance(b) > a.distance(c));
    }

    @Test
    public void testObject2() throws Exception {
        final Wrapped l1 = Wrapper.wrapObject(Arrays.asList(1, 2, 3));
        final Wrapped l1_prime = Wrapper.wrapObject(Arrays.asList(1, 2, 3));
        final Wrapped l2 = Wrapper.wrapObject(Arrays.asList(0, 1, 2, 3));
        assertFalse(Double.isInfinite(l1.distance(l1_prime)));
        assertFalse(Double.isInfinite(l1.distance(l2)));
        assertEquals(0D, l1.distance(l1_prime), 1e-5D);
        System.out.println(l1.distance(l2));
    }

    @Test
    public void testObject3() throws Exception {
        final List<Person> l1 = new LinkedList<>();
        final List<Person> l2 = new LinkedList<>();
        final Wrapped wl1 = Wrapper.wrapObject(l1);
        final Wrapped wl2 = Wrapper.wrapObject(l2);
        assertEquals(0D, wl1.distance(wl1), 1e-5D);
        assertEquals(0D, wl2.distance(wl2), 1e-5D);
        assertFalse(Double.isInfinite(wl1.distance(wl2)));
        System.out.println(wl1.distance(wl2));
    }

    @Test
    public void testObject4() throws Exception {
        final List<Person> l1 = new LinkedList<>();
        final Person p = new Person(10, new Date(), "a", "street", new int[] {1, 2});
        l1.add(p);
        final List<Person> l2 = new LinkedList<>();
        l2.add(p);
        final Wrapped wl1 = Wrapper.wrapObject(l1);
        final Wrapped wl2 = Wrapper.wrapObject(l2);
        assertEquals(0D, wl1.distance(wl1), 1e-5D);
        assertEquals(0D, wl2.distance(wl2), 1e-5D);
        assertFalse(Double.isInfinite(wl1.distance(wl2)));
        System.out.println(wl1.distance(wl2));
    }

    @Test
    public void testObject5() throws Exception {
        final List<Person> l1 = new LinkedList<>();
        final long currentTime = System.currentTimeMillis();
        l1.add(new Person(10, new Date(currentTime), "a", "street", new int[] {1, 2}));
        final List<Person> l2 = new LinkedList<>();
        l2.add(new Person(10, new Date(currentTime), "a", "street", new int[] {1, 2}));
        final Wrapped wl1 = Wrapper.wrapObject(l1);
        final Wrapped wl2 = Wrapper.wrapObject(l2);
        assertEquals(0D, wl1.distance(wl1), 1e-5D);
        assertEquals(0D, wl2.distance(wl2), 1e-5D);
        assertFalse(Double.isInfinite(wl1.distance(wl2)));
        System.out.println(wl1.distance(wl2));
    }

    @Test
    public void testObject6() throws Exception {
        final List<Person> l1 = new LinkedList<>();
        final Person p = new Person(10, new Date(), "a", "street", new int[] {1, 2});
        l1.add(p);
        l1.add(p);
        final List<Person> l2 = new LinkedList<>();
        l2.add(p);
        l2.add(p);
        final Wrapped wl1 = Wrapper.wrapObject(l1);
        final Wrapped wl2 = Wrapper.wrapObject(l2);
        assertEquals(0D, wl1.distance(wl1), 1e-5D);
        assertEquals(0D, wl2.distance(wl2), 1e-5D);
        assertFalse(Double.isInfinite(wl1.distance(wl2)));
        System.out.println(wl1.distance(wl2));
    }

    @Test
    public void testObject7() throws Exception {
        final List<Person> l1 = new LinkedList<>();
        final long currentTime = System.currentTimeMillis();
        l1.add(new Person(10, new Date(currentTime), "a", "street", new int[] {1, 2}));
        l1.add(new Person(10, new Date(currentTime), "a", "street", new int[] {1, 2}));
        final List<Person> l2 = new LinkedList<>();
        l2.add(new Person(10, new Date(currentTime), "a", "street", new int[] {1, 2}));
        l2.add(new Person(10, new Date(currentTime), "a", "street", new int[] {1, 2}));
        final Wrapped wl1 = Wrapper.wrapObject(l1);
        final Wrapped wl2 = Wrapper.wrapObject(l2);
        assertEquals(0D, wl1.distance(wl1), 1e-5D);
        assertEquals(0D, wl2.distance(wl2), 1e-5D);
        assertFalse(Double.isInfinite(wl1.distance(wl2)));
        System.out.println(wl1.distance(wl2));
    }

    @Test
    public void testObject8() throws Exception {
        final List<Person> l1 = new LinkedList<>();
        final long currentTime = System.currentTimeMillis();
        l1.add(new Person(10, new Date(currentTime), "a", "street1", new int[] {1, 2}));
        l1.add(new Person(11, new Date(currentTime), "b", "street2", new int[] {1, 3}));
        final List<Person> l2 = new LinkedList<>();
        l2.add(new Person(10, new Date(currentTime), "a", "street3", new int[] {1, 9, 2}));
        l2.add(new Person(12, new Date(currentTime), "c", "street4", new int[] {0, 1}));
        final Wrapped wl1 = Wrapper.wrapObject(l1);
        final Wrapped wl2 = Wrapper.wrapObject(l2);
        assertEquals(0D, wl1.distance(wl1), 1e-5D);
        assertEquals(0D, wl2.distance(wl2), 1e-5D);
        assertFalse(Double.isInfinite(wl1.distance(wl2)));
        System.out.println(wl1.distance(wl2));
    }

    @Test
    public void testObject9() throws Exception {
        final List<Person> l1 = new LinkedList<>();
        final long currentTime = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            l1.add(new Person(i, new Date(currentTime), "a" + i, "addr" + i, new int[] {i, 1 - i, 2 + i}));
        }
        final List<Person> l2 = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            l2.add(new Person(i, new Date(currentTime), "a" + i, "addr" + i, new int[] {i, 1 - i, 2 + i}));
        }
        final Wrapped wl1 = Wrapper.wrapObject(l1);
        final Wrapped wl2 = Wrapper.wrapObject(l2);
        assertEquals(0D, wl1.distance(wl1), 1e-5D);
        assertEquals(0D, wl2.distance(wl2), 1e-5D);
        assertFalse(Double.isInfinite(wl1.distance(wl2)));
        System.out.println(wl1.distance(wl2));
    }
}
