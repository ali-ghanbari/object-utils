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

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Ali Ghanbari
 */
public class PerformanceTest {
    private static class Node {
        final int intField;
        final double doubleField;
        final String id;
        final Object[] nums;
        final Node next;

        public Node(final Node next) {
            final Random random = new Random();
            this.intField = random.nextInt();
            this.doubleField = random.nextDouble();
            final StringBuilder sb = new StringBuilder("id:");
            for (int __ = 0; __ < 10; __++) {
                sb.append(' ').append(random.nextLong());
            }
            this.id = sb.toString();
            this.nums = new Object[randArrayLen(random)];
            for (int i = 0; i < this.nums.length; i++) {
                this.nums[i] = createArray(i, random);
            }
            this.next = next;
        }

        private static Object createArray(final int index,
                                          final Random random) {
            final Object res;
            if (index % 3 == 0) {
                final double[] arr = new double[randArrayLen(random)];
                Arrays.fill(arr, random.nextDouble());
                res = arr;
            } else if (index % 3 == 1) {
                final long[] arr = new long[randArrayLen(random)];
                Arrays.fill(arr, random.nextLong());
                res = arr;
            } else {
                final int[] arr = new int[randArrayLen(random)];
                Arrays.fill(arr, random.nextInt());
                res = arr;
            }
            return res;
        }

        private static int randArrayLen(final Random random) {
            final int len = random.nextInt() % 100;
            return len > 0 ? len : 10;
        }
    }

    static class ComplexLinkedList {
        Node head;

        void add() {
            this.head = new Node(this.head);
        }
    }

    private static ComplexLinkedList cll;

    @BeforeClass
    public static void setUp() {
        cll = new ComplexLinkedList();
        for (int i = 0; i < 1000; i++) {
            cll.add();
        }
    }

    @Test
    public void testDeepHashCodePerformance1() throws Exception {
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            ObjectUtils.deepHashCode(cll);
        }
        System.out.printf("%d ms%n", System.currentTimeMillis() - start);
    }

    @Test
    public void testDeepHashCodePerformance2() throws Exception {
        final int N = 100;
        long sum = 0;
        for (int i = 0; i < N; i++) {
            final long start = System.currentTimeMillis();
            ObjectUtils.deepHashCode(cll);
            sum += System.currentTimeMillis() - start;
        }
        System.out.printf("%f ms%n", sum / (double) N);
    }

    class IP1 extends InclusionPredicate {
        @Override
        public boolean test(Field field) {
            for (int i = 0; i < 10; i++) {
                field.getDeclaringClass().getName();
            }
            return field.getName().length() < 100;
        }
    }

    @Test
    public void testDeepHashCodePerformance3() throws Exception {
        final InclusionPredicate ip = new IP1();
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            ObjectUtils.deepHashCode(cll, ip);
        }
        System.out.printf("%d ms%n", System.currentTimeMillis() - start);
    }

    @Test
    public void testWrappingCodePerformance1() throws Exception {
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Wrapper.wrapObject(cll).unwrap();
        }
        System.out.printf("%d ms%n", System.currentTimeMillis() - start);
    }

    @Test
    public void testWrappingCodePerformance2() throws Exception {
        final InclusionPredicate ip = new IP1();
        final ComplexLinkedList template = null;
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Wrapper.wrapObject(cll, ip).unwrap(template);
        }
        System.out.printf("%d ms%n", System.currentTimeMillis() - start);
    }

    @Test
    public void testWrappingCodePerformance3() throws Exception {
        final InclusionPredicate ip = new IP1();
        final ComplexLinkedList template = null;
        final int N = 100;
        long sum = 0;
        for (int i = 0; i < N; i++) {
            final long start = System.currentTimeMillis();
            Wrapper.wrapObject(cll, ip).unwrap(template);
            sum += System.currentTimeMillis() - start;
        }
        System.out.printf("%f ms%n", sum / (double) N);
    }
}
