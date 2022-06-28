package edu.iastate.objectutils;

/*
 * #%L
 * Object Utilities
 * %%
 * Copyright (C) 2019 - 2022 Iowa State University
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

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectUtilsTest {

    @Test
    public void testDeepEquals1() {
        final ObjectUtils ou = ObjectUtils.build();
        final B obj1 = new C1(1.31, 10, "hello");
        final B obj2 = new C1(1.31, 10, "hello");
        assertTrue(ou.deepEquals(ou.makeSerializable(obj1), ou.makeSerializable(obj2)));
        final B obj3 = new C1(1.31, 10, "hello fresh");
        assertFalse(ou.deepEquals(ou.makeSerializable(obj1), ou.makeSerializable(obj3)));
        assertFalse(ou.deepEquals(ou.makeSerializable(obj2), ou.makeSerializable(obj3)));
    }

    @Test
    @Ignore
    public void testDeepEquals2() {
        final ObjectUtils ou = ObjectUtils.build();
        final HashSet<String> strings1 = new HashSet<>();
        strings1.add("hello");
        strings1.add("world!");
        strings1.add("how");
        strings1.add("are");
        strings1.add("you?");
        strings1.add("is");
        strings1.add("everything");
        strings1.add("OK?");
        strings1.add("R U sure?!");
        final HashSet<String> strings2 = new HashSet<>();
        strings2.add("is");
        strings2.add("hello");
        strings2.add("world!");
        strings2.add("OK?");
        strings2.add("how");
        strings2.add("are");
        strings2.add("you?");
        strings2.add("R U sure?!");
        strings2.add("everything");
        assertTrue(ou.deepEquals(ou.makeSerializable(strings1), ou.makeSerializable(strings2)));
    }

    @Test
    public void testDeepEquals3() {
        final ObjectUtils ou = ObjectUtils.build();
        final Object[] objects1 = new Object[2];
        final Object[] objects2 = new Object[2];
        objects1[0] = objects2;
        objects1[1] = 1;
        objects2[0] = objects1;
        objects2[1] = 1;
        assertTrue(ou.deepEquals(ou.makeSerializable(objects1), ou.makeSerializable(objects2)));
    }

    @Test
    public void testDeepEquals4() {
        final ObjectUtils ou = ObjectUtils.build();
        final Object[] objects1 = new Object[2];
        final Object[] objects2 = new Object[2];
        objects1[0] = objects2;
        objects1[1] = 1;
        objects2[0] = objects1;
        objects2[1] = 1;
        assertTrue(ou.deepEquals(ou.makeSerializable(objects1), ou.makeSerializable(objects2)));
    }

    @Test
    public void testDeepEquals5() {
        final ObjectUtils ou = ObjectUtils.build();
        final Student s1 = new Student("a", "b", 3.D);
        final Student s2 = new Student("c", "d", 4.D);
        assertFalse(ou.deepEquals(ou.makeSerializable(s1), ou.makeSerializable(s2)));
    }

    @Test
    public void testDeepEquals6() {
        final ObjectUtils ou = ObjectUtils.build();
        final Object[] objects1 = {
                1,
                2.718D,
                4L,
                40.21F,
                null
        };
        assertTrue(ou.deepEquals(ou.makeSerializable(objects1), ou.makeSerializable(objects1)));
        final Object[] objects2 = {
                1,
                2.718D,
                4L,
                40.21F,
                null
        };
        assertTrue(ou.deepEquals(ou.makeSerializable(objects1), ou.makeSerializable(objects2)));
        objects2[objects2.length - 1] = objects2;
        assertFalse(ou.deepEquals(ou.makeSerializable(objects1), ou.makeSerializable(objects2)));
    }

    @Test
    public void testDeepEquals8() {
        ObjectUtils ou = ObjectUtils.build();
        final B obj1 = new C1(1.31, 10, "hello");
        final B obj2 = new C1(1.32, 10, "hello");
        assertFalse(ou.deepEquals(obj1, obj2));
        ou = ou.withMaxInheritanceDepth(1);
        assertTrue(ou.deepEquals(ou.makeSerializable(obj1), ou.makeSerializable(obj2)));
        ou = ObjectUtils.build().include(field -> !field.getName().equals("f"));
        assertTrue(ou.deepEquals(ou.makeSerializable(obj1), ou.makeSerializable(obj2)));
    }

    @Test
    public void testDeepEquals9() {
        ObjectUtils ou = ObjectUtils.build();
        final Object[] o1 = {"hello", new int[] {1, 2}, new Object[] {new int[] {1, 2}}, 1};
        final Object[] o2 = {"hello", new int[] {1, 2}, new Object[] {new int[] {1, 3}}, 1};
        assertFalse(ou.deepEquals(ou.makeSerializable(o1), ou.makeSerializable(o2)));
        ou = ou.withMaxDepth(2);
        assertTrue(ou.deepEquals(ou.makeSerializable(o1), ou.makeSerializable(o2)));
    }

    @Test
    public void testDeepEquals10() {
        ObjectUtils ou = ObjectUtils.build();
        assertFalse(ou.deepEquals(ou.makeSerializable(System.in), ou.makeSerializable(System.out)));
        assertFalse(ou.deepEquals(ou.makeSerializable(System.in), ou.makeSerializable(null)));
        assertTrue(ou.deepEquals(ou.makeSerializable(null), ou.makeSerializable(null)));
    }

    @Test
    public void testDeepEquals11() {
        ObjectUtils ou = ObjectUtils.build();
        assertFalse(ou.deepEquals(ou.makeSerializable(int.class), ou.makeSerializable(String.class)));
        assertTrue(ou.deepEquals(ou.makeSerializable(Boolean.class), ou.makeSerializable(Boolean.class)));
    }

    private static class Node {
        Node load;

        Node next;

        public Node(Node next) {
            this.next = next;
        }
    }

    @Test
    public void testDeepEquals12() {
        ObjectUtils ou = ObjectUtils.build();
        Node l1 = new Node(new Node(new Node(null)));
        l1.next.load = l1;
        Node l2 = new Node(new Node(new Node(null)));
        l2.next.next.load = l2;
        assertFalse(ou.deepEquals(ou.makeSerializable(l1), ou.makeSerializable(l2)));
    }
}