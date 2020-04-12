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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static edu.utdallas.objectutils.CommonUtils.*;


public class MoreWrapperTest {
    private static final int COUNT = 50;

    private final Random random;

    public MoreWrapperTest() {
        this.random = new Random();
    }

    @Test
    public void wrapIntArrayMap() throws Exception {
        Map<IntArray, String> map = new HashMap<>();
        final IntArray[] arr = new IntArray[COUNT];
        for (int i = 0; i < COUNT; i++) {
            arr[i] = generateIntKey();
            map.put(arr[i], "s" + i);
        }
        final Wrapped wrapped = Wrapper.wrapObject(map);
        map = wrapped.unwrap();
        for (int i = 0; i < COUNT; i++) {
            assertEquals("s" + i, map.get(arr[i]));
        }
    }

    private static class IntArray {
        private final int[] array;

        public IntArray(int[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IntArray)) return false;
            IntArray intArray = (IntArray) o;
            return Arrays.equals(array, intArray.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

    private IntArray generateIntKey() {
        final int[] key = new int[COUNT];
        for (int i = 0; i < COUNT; i++) {
            key[i] = this.random.nextInt();
        }
        return new IntArray(key);
    }

    @Test
    public void wrapDoubleArrayMap() throws Exception {
        Map<DoubleArray, String> map = new HashMap<>();
        final DoubleArray[] arr = new DoubleArray[COUNT];
        for (int i = 0; i < COUNT; i++) {
            arr[i] = generateDoubleKey();
            map.put(arr[i], "s" + i);
        }
        final Wrapped wrapped = Wrapper.wrapObject(map);
        map = wrapped.unwrap();
        for (int i = 0; i < COUNT; i++) {
            assertEquals("s" + i, map.get(arr[i]));
        }
    }

    private static class DoubleArray {
        private final double[] array;

        public DoubleArray(double[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DoubleArray)) return false;
            DoubleArray that = (DoubleArray) o;
            return Arrays.equals(array, that.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

    private DoubleArray generateDoubleKey() {
        final double[] key = new double[COUNT];
        for (int i = 0; i < COUNT; i++) {
            key[i] = Math.log(Math.abs(this.random.nextDouble()));
        }
        return new DoubleArray(key);
    }

    @Test
    public void wrapDoubleMap() throws Exception {
        Map<Double, String> map = new HashMap<>();
        final double[] arr = new double[COUNT];
        for (int i = 0; i < COUNT; i++) {
            arr[i] = this.random.nextDouble();
            map.put(arr[i], "s" + i);
        }
        final Wrapped wrapped = Wrapper.wrapObject(map);
        map = wrapped.unwrap();
        for (int i = 0; i < COUNT; i++) {
            assertEquals("s" + i, map.get(arr[i]));
        }
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
    public void testEqualObject1() throws Exception {
        final long currentTime = System.currentTimeMillis();
        Wrapped a = Wrapper.wrapObject(new Person(10, new Date(currentTime), "a", "addr1", new int[]{0, 1, 2}));
        Wrapped a_prime = Wrapper.wrapObject(new Person(10, new Date(currentTime), "a", "addr1", new int[]{0, 1, 2}));
        assertEquals(a, a_prime);
    }

    @Test
    public void testCyclicObjectsEqual1() throws Exception {
        final Date dob = new Date();
        final Object[] oa2 = new Object[5];
        final Object[] oa1 = new Object[5];
        oa1[0] = oa2;
        oa1[1] = new int[] {1, 2};
        oa1[2] = "hello";
        oa1[3] = 1;
        oa1[4] = new Person(10, dob, "a", "street", (int[]) oa1[1]);
        ///
        oa2[0] = oa1;
        oa2[1] = new int[] {1, 2};
        oa2[2] = "hello";
        oa2[3] = 1;
        oa2[4] = new Person(10, dob, "a", "street", (int[]) oa2[1]);

        final Wrapped w1 = Wrapper.wrapObject(oa1);
        final Wrapped w2 = Wrapper.wrapObject(oa2);
        assertEquals(w1, w2);
    }

    @Test
    public void testStringArr2() throws Exception {
        final Wrapped a = Wrapper.wrapObject(new String[] {"a", "b"});
        final Wrapped b = Wrapper.wrapObject(new String[] {null, "b"});
        assertNotEquals(a, b);
    }

    // FAILS ON JDK 11
    @Test
    public void testStackOverflowWhenSerializingWrappedComplexObj() throws Exception {
        Object i1 = generateAVeryComplexObject();
        Wrapped w1 = Wrapper.wrapObject(i1);
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(w1);
            oos.flush();
        }
    }
}
