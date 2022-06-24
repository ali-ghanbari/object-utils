package edu.iastate.objectutils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectUtilsTest {

    @Test
    public void testDeepEquals1() {
        final ObjectUtils ou = ObjectUtils.build();
        final B obj1 = new C1(1.31, 10, "hello");
        final B obj2 = new C1(1.31, 10, "hello");
        assertTrue(ou.deepEquals(obj1, obj2));
        final B obj3 = new C1(1.31, 10, "hello fresh");
        assertFalse(ou.deepEquals(obj1, obj3));
        assertFalse(ou.deepEquals(obj2, obj3));
    }

    @Test
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
        assertTrue(ou.deepEquals(strings1, strings2));
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
        assertTrue(ou.deepEquals(objects1, objects2));
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
        assertTrue(ou.deepEquals(objects1, objects2));
    }

    @Test
    public void testDeepEquals5() {
        final ObjectUtils ou = ObjectUtils.build();
        final Student s1 = new Student("a", "b", 3.D);
        final Student s2 = new Student("c", "d", 4.D);
        assertFalse(ou.deepEquals(s1, s2));
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
        assertTrue(ou.deepEquals(objects1, objects1));
        final Object[] objects2 = {
                1,
                2.718D,
                4L,
                40.21F,
                null
        };
        assertTrue(ou.deepEquals(objects1, objects2));
        objects2[objects2.length - 1] = objects2;
        assertFalse(ou.deepEquals(objects1, objects2));
    }

    @Test
    public void testDeepEquals7() {
        final ObjectUtils ou = ObjectUtils.build();
        Object[] oa1 = new Object[0];
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            oa1 = Arrays.copyOf(oa1, oa1.length + 1);
            if ((oa1.length - 1) % 2 == 0) {
                oa1[oa1.length - 1] = sb.toString();
            } else {
                oa1[oa1.length - 1] = oa1;
            }
        }
        Object[] oa2 = new Object[0];
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            oa2 = Arrays.copyOf(oa2, oa2.length + 1);
            if ((oa2.length - 1) % 2 == 0) {
                oa2[oa2.length - 1] = sb.toString();
            } else {
                oa2[oa2.length - 1] = oa2;
            }
        }
        assertTrue(ou.deepEquals(oa1, oa2));
    }

    @Test
    public void testDeepEquals8() {
        ObjectUtils ou = ObjectUtils.build();
        final B obj1 = new C1(1.31, 10, "hello");
        final B obj2 = new C1(1.32, 10, "hello");
        assertFalse(ou.deepEquals(obj1, obj2));
        ou = ou.withMaxInheritanceDepth(1);
        assertTrue(ou.deepEquals(obj1, obj2));
        ou = ObjectUtils.build().include(field -> !field.getName().equals("f"));
        assertTrue(ou.deepEquals(obj1, obj2));
    }

    @Test
    public void testDeepEquals9() {
        ObjectUtils ou = ObjectUtils.build();
        final Object[] o1 = {"hello", new int[] {1, 2}, new Object[] {new int[] {1, 2}}, 1};
        final Object[] o2 = {"hello", new int[] {1, 2}, new Object[] {new int[] {1, 3}}, 1};
        assertFalse(ou.deepEquals(o1, o2));
        ou = ou.withMaxDepth(2);
        assertTrue(ou.deepEquals(o1, o2));
    }

    @Test
    public void testDeepEquals10() {
        ObjectUtils ou = ObjectUtils.build();
        assertFalse(ou.deepEquals(System.in, System.out));
        assertFalse(ou.deepEquals(System.in, null));
        assertTrue(ou.deepEquals(null, null));
    }
}