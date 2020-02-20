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
}
