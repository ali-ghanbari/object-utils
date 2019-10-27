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

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.*;

import static edu.utdallas.objectutils.ModificationPredicate.YES;

import static org.junit.Assert.*;

/**
 * @author Ali Ghanbari
 */
public class WrapperTest {
    @Test
    public void wrapBoolean() throws Exception {
        Wrapped wrapped = Wrapper.wrapBoolean(true);
        assertTrue((Boolean) wrapped.unwrap());
        wrapped = Wrapper.wrapBoolean(false);
        assertFalse((Boolean) wrapped.unwrap(YES));
    }

    @Test
    public void wrapByte() throws Exception {
        Wrapped wrapped = Wrapper.wrapByte((byte) 10);
        assertEquals(10, ((Byte) wrapped.unwrap()).intValue());
        wrapped = Wrapper.wrapByte((byte) 20);
        assertEquals(20, ((Byte) wrapped.unwrap(YES)).intValue());
    }

    @Test
    public void wrapChar() throws Exception {
        Wrapped wrapped = Wrapper.wrapChar('a');
        assertEquals((int) 'a', ((Character) wrapped.unwrap()).charValue());
        wrapped = Wrapper.wrapChar('b');
        assertEquals((int) 'b', ((Character) wrapped.unwrap(YES)).charValue());
    }

    @Test
    public void wrapDouble() throws Exception {
        Wrapped wrapped = Wrapper.wrapDouble(10.D);
        assertEquals(10.D, (Double) wrapped.unwrap(), 0.0001D);
        wrapped = Wrapper.wrapDouble(11.D);
        assertEquals(11.D, (Double) wrapped.unwrap(YES), 0.0001D);
    }

    @Test
    public void wrapFloat() throws Exception {
        Wrapped wrapped = Wrapper.wrapFloat(10.F);
        assertEquals(10.F, (Float) wrapped.unwrap(), 0.0001F);
        wrapped = Wrapper.wrapFloat(11.F);
        assertEquals(11.F, (Float) wrapped.unwrap(YES), 0.0001F);
    }

    @Test
    public void wrapInt() throws Exception {
        Wrapped wrapped = Wrapper.wrapInt(10);
        assertEquals(10, ((Integer) wrapped.unwrap()).intValue());
        wrapped = Wrapper.wrapInt(20);
        assertEquals(20, ((Integer) wrapped.unwrap(YES)).intValue());
    }

    @Test
    public void wrapLong() throws Exception {
        Wrapped wrapped = Wrapper.wrapLong(10L);
        assertEquals(10L, ((Long) wrapped.unwrap()).longValue());
        wrapped = Wrapper.wrapLong(20L);
        assertEquals(20L, ((Long) wrapped.unwrap(YES)).longValue());
    }

    @Test
    public void wrapShort() throws Exception {
        Wrapped wrapped = Wrapper.wrapShort((short) 10);
        assertEquals(10, ((Short) wrapped.unwrap()).shortValue());
        wrapped = Wrapper.wrapShort((short) 20);
        assertEquals(20, ((Short) wrapped.unwrap(YES)).shortValue());
    }

    @Test
    public void wrapString() throws Exception {
        Wrapped wrapped = Wrapper.wrapString("hello");
        assertEquals("hello", wrapped.unwrap());
        wrapped = Wrapper.wrapString("world");
        assertEquals("world", wrapped.unwrap(YES));
    }

    private static class A {
        public static final String AX = "10";
        private static int _AX = 10;
        private final int ax;
        private int ah;
        protected int al;

        A(int ax, int ah, int al) {
            this.ax = ax;
            this.ah = ah;
            this.al = al;
        }

        public int getAx() {
            return ax;
        }

        public int getAh() {
            return ah;
        }

        public int getAl() {
            return al;
        }

        public boolean checkRep() {
            return _AX == 10;
        }
    }

    private String myExternalField = "HELLO!";

    private class B extends A {
        public final String name = "BX";
        private final int value;
        final R r;

        B(int value) {
            super(10, 10, 10);
            this.value = value;
            this.r = new R("bx", 10);
        }

        public int getValue() {
            return value;
        }

        public R getR() {
            return r;
        }

        public String getExternalField() {
            return myExternalField;
        }
    }

    private final class R {
        private final String name;
        private final int val;

        R(String name, int val) {
            this.name = name;
            this.val = val;
        }

        public String getName() {
            return name;
        }

        public int getVal() {
            return val;
        }
    }

    @Test
    public void wrapObject() throws Exception {
        final B b = new B(30);
        checkRep(b);
        Wrapped wrapped = Wrapper.wrapObject(b);
        final B bPrime = wrapped.unwrap();
        checkRep(b);
        checkRep(bPrime);
        assertEquals("HELLO!", bPrime.getExternalField());
        this.myExternalField = "WORLD!";
        wrapped = Wrapper.wrapObject(b);
        final B bDoublePrime = wrapped.unwrap(YES);
        checkRep(b);
        checkRep(bPrime);
        checkRep(bDoublePrime);
        assertEquals("WORLD!", bDoublePrime.getExternalField());
    }

    private void checkRep(final B b) {
        final R r = b.r;
        if (r.name.equals("bx") && r.val == 10) {
            if (b.value == 30) {
                if (b.name.equals("BX")) {
                    if (b.getAh() == 10 && b.getAl() == 10 && b.getAx() == 10) {
                        if (b.checkRep()) {
                            if (!B.AX.equals("10")) {
                                throw new IllegalStateException("bad AX");
                            }
                        } else {
                            throw new IllegalStateException("bad _AX");
                        }
                    } else {
                        throw new IllegalStateException("bad ah | al | ax");
                    }
                } else {
                    throw new IllegalStateException("bad name");
                }
            } else {
                throw new IllegalStateException("bad value");
            }
        } else {
            throw new IllegalStateException("bad r");
        }
    }

    private static class Record {
        private final String recordId;
        private String recordValue;

        Record(String recordId, String recordValue) {
            this.recordId = recordId;
            this.recordValue = recordValue;
        }

        public String getRecordId() {
            return recordId;
        }

        public String getRecordValue() {
            return recordValue;
        }

        public void setRecordValue(String recordValue) {
            this.recordValue = recordValue;
        }
    }

    private static class Records {
        private final List<Record> records;

        Records(final Record... records) {
            this.records = Arrays.asList(records);
        }
    }

    private static class Student {
        private final String name;
        private final int age;
        private final Records records;

        Student(String name, int age, final Record... records) {
            this.name = name;
            this.age = age;
            this.records = new Records(records);
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Records getRecords() {
            return records;
        }
    }

    @Test
//    @Ignore
    public void wrapObject1() throws Exception {
        final Record r1 = new Record("r1", "30");
        final Record r2 = new Record("r2", "40");
        final Student s1 = new Student("Ali", 28, r1, r2);
        final Student s2 = new Student("Ali", 28, r1, r2);
//        assertEquals(s1, s2);
        final Wrapped w1 = Wrapper.wrapObject(s1);
        final Wrapped w2 = Wrapper.wrapObject(s2);
        assertEquals(w1, w2);
    }

    @Test
//    @Ignore
    public void wrapObject2() throws Exception {
        final Record r1 = new Record(null, "30");
        final Record r2 = new Record("r2", null);
        final Student s1 = new Student("Ali", 28, r1, r2);
        final Student s2 = new Student("Ali", 28, r1, r2);
//        assertEquals(s1, s2);
        final Wrapped w1 = Wrapper.wrapObject(s1);
        final Wrapped w2 = Wrapper.wrapObject(s2);
        assertEquals(w1, w2);
    }

    private static class ListNode {
        final int value;
        final ListNode next;

        ListNode(int value, ListNode next) {
            this.value = value;
            this.next = next;
        }

        public int getValue() {
            return value;
        }
    }

    private static class IntLinkedList {
        private ListNode head;

        IntLinkedList() {
            this.head = null;
        }

        void add(int value) {
            this.head = new ListNode(value, this.head);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('[');
            ListNode cursor = this.head;
            while (cursor != null) {
                sb.append(cursor.value);
                cursor = cursor.next;
                if (cursor != null) {
                    sb.append(',');
                }
            }
            sb.append(']');
            return sb.toString();
        }
    }

    @Test
    public void wrapObject3() throws Exception {
        final IntLinkedList ill = new IntLinkedList();
        ill.add(1);
        ill.add(0);
        ill.add(-1);
        assertEquals("[-1,0,1]", ill.toString());
        Wrapped wrapped = Wrapper.wrapObject(ill);
        IntLinkedList ill2 = wrapped.unwrap();
        assertEquals("[-1,0,1]", ill2.toString());
        IntLinkedList ill3 = wrapped.unwrap(YES);
        assertEquals("[-1,0,1]", ill3.toString());
    }

    private static class Bad1 {
        private static Bad2 evil = new Bad2();
    }

    private static class Bad2 {
        private static Bad1 evil = new Bad1();
    }

    @Test
    public void wrapObject4() throws Exception {
        final Bad1 bad = new Bad1();
        Wrapped wrapped = Wrapper.wrapObject(bad);
    }

    private static class Cyclic {
        static class Node {
            Node next;
            final int value;

            Node(Node next, int value) {
                this.next = next;
                this.value = value;
            }
        }

        final Node head;

        Cyclic() {
            final Node next = new Node(null, 10);
            this.head = new Node(next, 11);
            next.next = this.head;
        }

        @Override
        public String toString() {
            return String.format("%d,%d,%d",
                    this.head.value,
                    this.head.next.value,
                    this.head.next.next.value);
        }
    }

    @Test
    public void wrapObject5() throws Exception {
        final Cyclic cyclic = new Cyclic();
        assertEquals("11,10,11", cyclic.toString());
        final Wrapped wrapped = Wrapper.wrapObject(cyclic);
        Cyclic cyclic2 = wrapped.unwrap();
        assertEquals(cyclic.toString(), cyclic2.toString());
        assertEquals("11,10,11", cyclic2.toString());
        cyclic2 = wrapped.unwrap(YES);
        assertEquals(cyclic.toString(), cyclic2.toString());
        assertEquals("11,10,11", cyclic2.toString());
    }

    @Test
    public void wrappedObjectEqualsTest1() throws Exception {
        IntLinkedList ill1 = new IntLinkedList();
        ill1.add(0);
        ill1.add(1);
        Wrapped w1 = Wrapper.wrapObject(ill1);
        IntLinkedList ill2 = new IntLinkedList();
        ill2.add(0);
        ill2.add(1);
        Wrapped w2 = Wrapper.wrapObject(ill2);
        assertEquals(w1, w2);
    }

    private static class SelfList {
        SelfList sl;

        SelfList() {
            sl = this;
        }
    }

    @Test
    public void wrappedObjectEqualsTest2() throws Exception {
        SelfList sl1 = new SelfList();
        Wrapped w1 = Wrapper.wrapObject(sl1);
//        assertEquals(w1, ((WrappedObject) w1).getWrappedFieldValues()[0]);
        SelfList sl2 = new SelfList();
        Wrapped w2 = Wrapper.wrapObject(sl2);
        assertEquals(w1, w2);
    }

    @Test
    public void wrappedArrayTest1() throws Exception {
        final int[] arr1 = {1, 2, 3};
        final int[] arr2 = {1, 2, 3};
        assertArrayEquals(arr1, arr2);
        final Wrapped wa1 = Wrapper.wrapObject(arr1);
        final Wrapped wa2 = Wrapper.wrapObject(arr2);
        assertEquals(wa1, wa2);
        assertEquals("[1, 2, 3]", wa1.print());
    }

    @Test
    public void wrappedArrayTest2() throws Exception {
        final int[][] arr1 = {{1, 2, 3}, {2, 3, 4}};
        final int[][] arr2 = {{1, 2, 3}, {2, 3, 4}};
        assertArrayEquals(arr1, arr2);
        final Wrapped wa1 = Wrapper.wrapObject(arr1);
        final Wrapped wa2 = Wrapper.wrapObject(arr2);
        assertEquals(wa1, wa2);
    }

    @Test
    public void wrappedArrayTest3() throws Exception {
        final int[][] arr1 = {{1, 2, 3}, {2, 3, 4}};
        final int[][] arr2 = {{1, 2, 3}, {2, 5, 4}};
        final Wrapped wa1 = Wrapper.wrapObject(arr1);
        final Wrapped wa2 = Wrapper.wrapObject(arr2);
        assertNotEquals(wa1, wa2);
    }

    @Test
    public void wrappedArrayTest4() throws Exception {
        final int[][] arr1 = {{1, 2, 3}, null};
        final int[][] arr2 = {{1, 2, 3}, null};
        final Wrapped wa1 = Wrapper.wrapObject(arr1);
        final Wrapped wa2 = Wrapper.wrapObject(arr2);
        assertEquals(wa1, wa2);
    }

    @Test
    public void wrappedArrayTest5() throws Exception {
        final List l1 = new ArrayList();
        l1.add(1);
        l1.add(2);
        l1.add(3);
        final List l2 = new ArrayList();
        l2.add(3);
        l2.add(4);
        l2.add(5);
        final List[] arr1 = {l1, l2};
        final List[] arr2 = {l1, l2};
        assertArrayEquals(arr1, arr2);
        final Wrapped wa1 = Wrapper.wrapObject(arr1);
        final Wrapped wa2 = Wrapper.wrapObject(arr2);
        assertEquals(wa1.toString(), wa2.toString());
    }

    @Test
    public void wrappedArrayTest6() throws Exception {
        final Object[] objectArray = new Object[1];
        objectArray[0] = objectArray;
        final Wrapped wrapped = Wrapper.wrapObject(objectArray);
        final Object[] uw = (Object[]) wrapped.unwrap();
        //System.out.println(uw[0].getClass().getName());
        assertTrue(uw[0] == uw);
    }

    private static class TC {
        private final int[] f1 = {0, 1, 2};
        private final Integer[] f2 = {1, 2, 3};

        @Override
        public String toString() {
            return "TC{" +
                    "f1=" + Arrays.toString(f1) +
                    ", f2=" + Arrays.toString(f2) +
                    '}';
        }
    }

    @Test
    public void wrappedArrayTest7() throws Exception {
        final TC tc = new TC();
        final Wrapped wrapped = Wrapper.wrapObject(tc);
        assertEquals(tc.toString(), wrapped.unwrap().toString());
    }

    @Test
    public void wrappedObjectArrayEqualsTest1() throws Exception {
        Object oa1[] = new Object[0];
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            oa1 = Arrays.copyOf(oa1, oa1.length + 1);
            oa1[oa1.length - 1] = sb.toString();
        }
        Object oa2[] = new Object[0];
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            oa2 = Arrays.copyOf(oa2, oa2.length + 1);
            oa2[oa2.length - 1] = sb.toString();
        }
        assertArrayEquals(oa1, oa2);
        final Wrapped w1 = Wrapper.wrapObject(oa1);
        final Wrapped w2 = Wrapper.wrapObject(oa2);
        assertEquals(w1, w2);
    }

    @Test
    public void wrappedObjectArrayEqualsTest2() throws Exception {
        Object oa1[] = new Object[0];
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
        Object oa2[] = new Object[0];
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
        final Wrapped w1 = Wrapper.wrapObject(oa1);
        final Wrapped w2 = Wrapper.wrapObject(oa2);
        assertEquals(w1, w2);
    }

    @Test
    public void wrappedObjectArrayEqualsTest3() throws Exception {
        Object oa1[] = new Object[0];
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
        Object oa2[] = new Object[0];
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            oa2 = Arrays.copyOf(oa2, oa2.length + 1);
            if ((oa2.length - 1) % 2 == 0) {
                oa2[oa2.length - 1] = sb.toString();
            } else {
                oa2[oa2.length - 1] = oa1;
            }
        }
        final Wrapped w1 = Wrapper.wrapObject(oa1);
        final Wrapped w2 = Wrapper.wrapObject(oa2);
        assertNotEquals(w1, w2);
    }

    @Test
    public void wrappedObjectEqualsTest3() throws Exception {
        final List<String> l1 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            l1.add(sb.toString());
        }
        final List<String> l2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            l2.add(sb.toString());
        }
        assertEquals(l1, l2);
        final Wrapped w1 = Wrapper.wrapObject(l1);
        final Wrapped w2 = Wrapper.wrapObject(l2);
        System.out.println("-------------");
        assertEquals(w1, w2);
    }

    @Test
    public void wrappedObjectEqualsTest4() throws Exception {
        final Map<Integer, String> map1 = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            map1.put(i, sb.toString());
        }
        final Map<Integer, String> map2 = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            map2.put(i, sb.toString());
        }
        final Wrapped w1 = Wrapper.wrapObject(map1);
        final Wrapped w2 = Wrapper.wrapObject(map2);
        assertEquals(w1, w2);
    }

    @Test
    public void wrappedObjectEqualsTest5() throws Exception {
        final Set<String> set1 = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            set1.add(sb.toString());
        }
        final Set<String> set2 = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            set2.add(sb.toString());
        }
        final Wrapped w1 = Wrapper.wrapObject(set1);
        final Wrapped w2 = Wrapper.wrapObject(set2);
        assertEquals(w1, w2);
    }

    private static class Course {
        private final String name;
        private final int id;
        private final float grade;

        public Course(String name, int id, float grade) {
            this.name = name;
            this.id = id;
            this.grade = grade;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public float getGrade() {
            return grade;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Course course = (Course) o;
            return id == course.id &&
                    Float.compare(course.grade, grade) == 0 &&
                    Objects.equals(name, course.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, id, grade);
        }
    }

    private static class BSCStudent {
        private final String name;
        private final String id;
        private final Set<Course> ah;

        public BSCStudent(String name, String id) {
            this.name = name;
            this.id = id;
            this.ah = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public void addCourses(final Course... courses) {
            for (final Course course : courses) {
                this.ah.add(course);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BSCStudent that = (BSCStudent) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(id, that.id) &&
                    Objects.equals(ah, that.ah);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, id, ah);
        }
    }

    @Test
    public void testHashMap1() throws Exception {
        final Map<BSCStudent, String> students = new HashMap<>();
        final BSCStudent student1 = new BSCStudent("a", "a1");
        final BSCStudent student2 = new BSCStudent("a", "a1");
        final BSCStudent student3 = new BSCStudent("c", "c1");
        final Course course1 = new Course("os", 1, 19.5F);
        final Course course2 = new Course("ds", 2, 20.0F);
        final Course course3 = new Course("cn", 3, 17.0F);
        final Course course4 = new Course("pl", 4, 20.0F);
        final Course course5 = new Course("al", 5, 18.5F);
        student1.addCourses(course1, course2, course4);
        student2.addCourses(course1, course2, course4);
        student3.addCourses(course3, course5);
        students.put(student1, "good");
        students.put(student2, "bad");
        students.put(student3, "ugly");
        assertEquals(2, students.size());
        final Wrapped wrappedStudent1 = Wrapper.wrapObject(student1);
        final Wrapped wrappedStudent2 = Wrapper.wrapObject(student2);
        final Wrapped wrappedStudent3 = Wrapper.wrapObject(student3);
        final Map<Wrapped, String> wrappedStudents = new HashMap<>();
        wrappedStudents.put(wrappedStudent1, "good");
        wrappedStudents.put(wrappedStudent2, "bad");
        wrappedStudents.put(wrappedStudent3, "ugly");
        assertEquals(2, wrappedStudents.size());
        assertEquals(students, Wrapper.wrapObject(students).unwrap());
    }

    @Test
    public void testArrayElementsReference1() throws Exception {
        final BSCStudent student1 = new BSCStudent("a", "a1");
        final BSCStudent student2 = student1;
        final BSCStudent student3 = new BSCStudent("c", "c1");
        final Course course1 = new Course("os", 1, 19.5F);
        final Course course2 = new Course("ds", 2, 20.0F);
        final Course course3 = new Course("cn", 3, 17.0F);
        final Course course4 = new Course("pl", 4, 20.0F);
        final Course course5 = new Course("al", 5, 18.5F);
        student1.addCourses(course1, course2, course4);
        student3.addCourses(course3, course5);
        final Object[] students = {student1, student2, student3};
        assertSame(students[0], students[1]);
        final Object[] unwrapped = Wrapper.wrapObject(students).unwrap();
        assertEquals(students.length, unwrapped.length);
        assertSame(unwrapped[0], unwrapped[1]);
        assertArrayEquals(students, (Object[]) Wrapper.wrapObject(students).unwrap());
    }

    private static File createTempFile() throws Exception {
        File tempFile = File.createTempFile("object-utils-", "-io-test");
        tempFile.deleteOnExit();
        return tempFile;
    }

    private static <T> Wrapped wrapWriteAndRead(T object) throws Exception {
        final Wrapped wrapped = Wrapper.wrapObject(object);
        final File tempFile = createTempFile();
        try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
            oos.writeObject(wrapped);
        }
        try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile))) {
            return (Wrapped) ois.readObject();
        } catch (Throwable __) {
            fail();
        }
        return null; // unreachable
    }

    @Test
    public void testWrappedObjectIO1() throws Exception {
        final SelfList selfList = new SelfList();
        wrapWriteAndRead(selfList);
    }

    @Test
    public void testWrappedObjectIO2() throws Exception {
        final Map<BSCStudent, String> students = new HashMap<>();
        final BSCStudent student1 = new BSCStudent("a", "a1");
        final BSCStudent student2 = new BSCStudent("a", "a1");
        final BSCStudent student3 = new BSCStudent("c", "c1");
        final Course course1 = new Course("os", 1, 19.5F);
        final Course course2 = new Course("ds", 2, 20.0F);
        final Course course3 = new Course("cn", 3, 17.0F);
        final Course course4 = new Course("pl", 4, 20.0F);
        final Course course5 = new Course("al", 5, 18.5F);
        student1.addCourses(course1, course2, course4);
        student2.addCourses(course1, course2, course4);
        student3.addCourses(course3, course5);
        students.put(student1, "good");
        students.put(student2, "bad");
        students.put(student3, "ugly");
        final Wrapped wrapped = wrapWriteAndRead(students);
        assertEquals(students, wrapped.unwrap());
    }

    /*fails on JDK 10*/
    @Test
    public void testWrappedObjectIO3() throws Exception {
        final List<Integer> list = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        final Wrapped wrapped = wrapWriteAndRead(list);
        assertEquals(list, wrapped.unwrap());
    }

    @Test
    public void testWrappedObjectArrayNullElements1() throws Exception {
        final Object[] arr = {null, new Object[]{null, null}};
        assertArrayEquals(arr, arr);
        assertEquals(Wrapper.wrapObject(arr), Wrapper.wrapObject(arr));
        assertArrayEquals(arr, (Object[]) Wrapper.wrapObject(arr).unwrap());
    }

    private static class NullFieldsClass {
        private final Object nf = null;
        private final Object[] naf = {null, new Object[]{null, null}};
    }

    @Test
    public void testWrappedObjectNullFields1() throws Exception {
        final Wrapped w1 = Wrapper.wrapObject(new NullFieldsClass());
        final Wrapped w2 = Wrapper.wrapObject(new NullFieldsClass());
        assertEquals(w1, w2);
    }

    private static class SelectiveClass {
        private int a;
        private String b;
        private final float c;
        private long d;

        public SelectiveClass(int a, String b, float c, long d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SelectiveClass that = (SelectiveClass) o;
            return a == that.a &&
                    Float.compare(that.c, c) == 0 &&
                    d == that.d &&
                    Objects.equals(b, that.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b, c, d);
        }

        @Override
        public String toString() {
            return "SelectiveClass{" +
                    "a=" + a +
                    ", b='" + b + '\'' +
                    ", c=" + c +
                    ", d=" + d +
                    '}';
        }
    }

    @Test
    public void testWrappingObjectsWithIgnoredFields1() throws Exception {
        final InclusionPredicate ipIgnoreB = new InclusionPredicate() {
            @Override
            public boolean test(Field field) {
                return !field.getName().equals("b");
            }
        };
        final SelectiveClass objA = new SelectiveClass(10, "aa", 11, 23);
        Wrapped wrappedObjA = Wrapper.wrapObject(objA, ipIgnoreB);
        final SelectiveClass objB = new SelectiveClass(10, "bb", 11, 23);
        Wrapped wrappedObjB = Wrapper.wrapObject(objB, ipIgnoreB);
        assertEquals(wrappedObjA, wrappedObjB);
        final SelectiveClass objATemplate1 = new SelectiveClass(10, "xx", 12, 22);
        assertEquals(Wrapper.wrapObject(objA, ipIgnoreB).unwrap(objATemplate1).toString(),
                "SelectiveClass{a=10, b=\'xx\', c=11.0, d=23}");
        final SelectiveClass objATemplate2 = new SelectiveClass(10, "aa", 11, 23);
        assertEquals(Wrapper.wrapObject(objA, ipIgnoreB).unwrap(objATemplate2), objA);
        wrappedObjA = Wrapper.wrapObject(objA);
        wrappedObjB = Wrapper.wrapObject(objB);
        assertNotEquals(wrappedObjA, wrappedObjB);
    }

    @Test
    public void testWrappingObjectsWithIgnoredFields2() throws Exception {
        final InclusionPredicate ipIgnoreB = new InclusionPredicate() {
            @Override
            public boolean test(Field field) {
                return !field.getName().equals("b");
            }
        };
        SelectiveClass[] arr1 = new SelectiveClass[2];
        arr1[0] = new SelectiveClass(10, "aa", 11, 23);
        arr1[1] = new SelectiveClass(10, "bb", 11, 23);
        Wrapped wrappedArray1 = Wrapper.wrapObject(arr1, ipIgnoreB);
        SelectiveClass[] arr2 = new SelectiveClass[2];
        arr2[0] = new SelectiveClass(20, "aa", 21, 3);
        arr2[1] = new SelectiveClass(11, "bb", 12, 2);
        assertFalse(Arrays.equals(arr1, arr2));
        assertArrayEquals((SelectiveClass[]) wrappedArray1.unwrap(arr2), arr1);
    }

    @Test
    public void testArrayReference1() throws Exception {
        final double array[][] = new double[2][];
        array[0] = new double[]{1.2D, 2.1D};
        array[1] = array[0];
        Wrapped wrapped = Wrapper.wrapObject(array);
        double[][] unwrapped = wrapped.unwrap();
        unwrapped[0][0] = 3.14D;
        array[0][0] = 3.14D;
        assertArrayEquals(array, unwrapped);
    }

    @Test
    public void testArrayReference2() throws Exception {
        final double array[][] = new double[2][];
        array[0] = new double[]{1.2D, 2.1D};
        array[1] = array[0];
        Wrapped wrapped = Wrapper.wrapObject(array);
        final String prev = wrapped.print();
        double[][] unwrapped = wrapped.unwrap();
        unwrapped[0][0] = 3.14D;
        assertEquals(prev, wrapped.print());
    }

    @Test
    public void testArrayReference3() throws Exception {
        final int[] arr = {1, 2};
        final Wrapped wrapped = Wrapper.wrapObject(arr);
        final String prev = wrapped.print();
        final int[] unwrapped = wrapped.unwrap();
        unwrapped[0] = 0;
        assertEquals(prev, wrapped.print());
    }

    private static class OuterClass {
        InnerClass f1;
        InnerClass f2;
        String s;
    }

    private static class InnerClass {
        int[] f1;
        int[][] f2;
        OuterClass back;
        OuterClass forward;
    }

    @Test
    public void testObjectReference2() throws Exception {
        OuterClass outerClass = new OuterClass();
        {
            outerClass.f1 = outerClass.f2 = new InnerClass();
            outerClass.s = "hello";
            InnerClass innerClass = outerClass.f1;
            innerClass.f1 = new int[]{1, 2};
            innerClass.f2 = new int[][]{innerClass.f1, {3, 4}};
            innerClass.back = outerClass;
            innerClass.forward = new OuterClass();
        }
        Wrapped wrapped = Wrapper.wrapObject(outerClass);
        final String prev = wrapped.print();
        final OuterClass unwrapped = wrapped.unwrap();
        assertEquals(prev, Wrapper.wrapObject(unwrapped).print());
        assertSame(unwrapped.f1, unwrapped.f2);
        unwrapped.f1.f1[1] = 5;
        assertEquals(prev, wrapped.print());
    }

    @Test
    public void testWrappingObjectsWithIgnoredFields3() throws Exception {
        final InclusionPredicate ip = new InclusionPredicate() {
            @Override
            public boolean test(Field field) {
                return field.getName().matches("back|f1");
            }
        };
        OuterClass outerClass = new OuterClass();
        {
            outerClass.f1 = outerClass.f2 = new InnerClass();
            outerClass.s = "hello";
            InnerClass innerClass = outerClass.f1;
            innerClass.f1 = new int[]{1, 2};
            innerClass.f2 = new int[][]{innerClass.f1, {3, 4}};
            innerClass.back = outerClass;
            innerClass.forward = new OuterClass();
        }
        assertSame(outerClass.f1.f1, outerClass.f1.f2[0]);
        Wrapped wrapped = Wrapper.wrapObject(outerClass, ip);
        wrapped.unwrap(outerClass);
        assertSame(outerClass.f1.f1, outerClass.f1.f2[0]);
    }

    @Test
    public void test2DDoubleArray1() throws Exception {
        final double[][] arr1 = {
                {1D, 2D, 3D},
                {1.1D, 2.2D, 3.3D},
                {3.14D, 2.718D, 0.08D}
        };
        final double[][] arr2 = {
                {1D, 2D, 3D},
                {1.1D, 2.2D, 3.3D},
                {3.14D, 2.718D, 0.08D}
        };
        assertTrue(Arrays.deepEquals(arr1, arr2));
        final Wrapped wrapped1 = Wrapper.wrapObject(arr1);
        final Wrapped wrapped2 = Wrapper.wrapObject(arr2);
        assertEquals(wrapped1.hashCode(), wrapped2.hashCode());
        assertEquals(wrapped1, wrapped2);
        assertTrue(Arrays.deepEquals((double[][]) wrapped1.unwrap(), arr2));
        assertTrue(Arrays.deepEquals((double[][]) wrapped1.unwrap(arr2), arr1));

    }

    @Test
    public void testDoubleArray2() throws Exception {
        final double[] arr1 = {3.14D, -1D, -0.08D, 1};
        final double[] arr2 = {3.14D, -1D, -0.08D, 1};
        assertArrayEquals(arr1, arr2, 0.0);
        final Wrapped wrapped1 = Wrapper.wrapObject(arr1);
        final Wrapped wrapped2 = Wrapper.wrapObject(arr2);
        assertEquals(wrapped1.hashCode(), wrapped2.hashCode());
        assertEquals(wrapped1, wrapped2);
        assertArrayEquals((double[]) wrapped1.unwrap(), arr2, 0.0);
        assertArrayEquals((double[]) wrapped1.unwrap(arr2), arr1, 0.0);
    }

    @Test
    public void testClassObjects1() throws Exception {
        final Wrapped wc1 = Wrapper.wrapObject(String.class);
        final Wrapped wc2 = Wrapper.wrapObject(String.class);
        assertEquals(wc1, wc2);
        assertSame(wc1.unwrap(), wc2.unwrap());
        final Wrapped wc3 = Wrapper.wrapObject(String[].class);
        final Wrapped wc4 = Wrapper.wrapObject(int.class);
        assertNotEquals(wc3, wc4);
        assertSame(wc3.unwrap(), String[].class);
        assertNotSame(wc3.unwrap(), wc4.unwrap());
    }

    private enum Colors {
        RED,
        GREEN,
        BLUE
    }

    @Test
    public void testEnumObjects1() throws Exception {
        final Wrapped we1 = Wrapper.wrapObject(Colors.RED);
        final Wrapped we2 = Wrapper.wrapObject(Colors.BLUE);
        assertNotEquals(we1, we2);
        assertNotSame(we1.unwrap(), we2.unwrap());
        final Wrapped b1 = Wrapper.wrapObject(Colors.GREEN);
        final Wrapped b2 = Wrapper.wrapObject(Colors.GREEN);
        assertEquals(b1, b2);
        assertSame(b1.unwrap(), b2.unwrap());
    }

    private enum BigEnum {
        I1(1, "S1", Dummy.J1),
        I2(1, "B1", Dummy.J2),
        I3(1, "D1", new Enum[]{Dummy.J1, Dummy.J2});

        private int i;
        private final String s;
        private Object bl;

        BigEnum(int i, String s, Object bl) {
            this.i = i;
            this.s = s;
            this.bl = bl;
        }
    }

    private enum Dummy {
        J1(BigEnum.I1),
        J2(BigEnum.I2);

        private Enum e;

        Dummy(Enum e) {
            this.e = e;
        }
    }

    @Test
    public void testEnumObjects2() throws Exception {
        final Wrapped wrappedI1 = Wrapper.wrapObject(BigEnum.I1);
        final Wrapped wrappedI2 = Wrapper.wrapObject(BigEnum.I2);
        final Wrapped wrappedI3 = Wrapper.wrapObject(BigEnum.I3);
        assertEquals(BigEnum.I1, wrappedI1.unwrap(BigEnum.I1));
        wrappedI1.unwrap(BigEnum.I2);
        assertEquals(wrappedI1.print(), "I1:BigEnum@1{0:1,1:S1,2:@3}Dummy@3{0:null}");
        assertEquals(wrappedI2.print(), "I2:BigEnum@1{0:1,1:B1,2:@3}Dummy@3{0:null}");
        assertEquals(wrappedI3.print(), "I3:BigEnum@1{0:1,1:D1,2:@2}OBJ_ARRAY@2{0:@3,1:@3}Dummy@3{0:null}");
    }

    private enum E1 {
        F1;
    }

    private enum E2 {
        F1;
    }

    @Test
    public void testEnumObjects3() throws Exception {
        final Wrapped w1 = Wrapper.wrapObject(E1.F1);
        final Wrapped w2 = Wrapper.wrapObject(E2.F1);
        assertNotEquals(w1, w2);
    }

    private static class ClazzTestClass {
        private final Class<?> c1 = String.class;
        private final Field f1 = c1.getDeclaredFields()[2];
    }

    @Test
    public void testClassObjects3() throws Exception {
        final ClazzTestClass clazz = new ClazzTestClass();
        clazz.f1.setAccessible(true);
        Wrapped w = wrapWriteAndRead(clazz.f1);
        assertSame(((Field) w.unwrap()).getDeclaringClass(), String.class);
        w = wrapWriteAndRead(clazz);
        assertSame(((ClazzTestClass) w.unwrap()).c1, String.class);
    }

    @Test
    public void testShallowWrapping1() throws Exception {

    }
}