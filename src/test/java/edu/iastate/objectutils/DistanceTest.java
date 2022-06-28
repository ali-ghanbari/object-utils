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

import org.apache.commons.text.similarity.HammingDistance;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DistanceTest {
    @Test
    public void hammingDistance1() {
        final ObjectUtils ou = ObjectUtils.build();
        final HammingDistance hd = new HammingDistance();
        double d = hd.apply("hello", "hello");
        assertEquals(d, ou.extendedHammingDistance(ou.makeSerializable("hello"), ou.makeSerializable("hello")), 1e-5);
        d = hd.apply("he1lo", "he2lo");
        assertEquals(d, ou.extendedHammingDistance(ou.makeSerializable("he1lo"), ou.makeSerializable("he2lo")), 1e-5);
        d = hd.apply("world", "hello");
        assertEquals(d, ou.extendedHammingDistance(ou.makeSerializable("world"), ou.makeSerializable("hello")), 1e-5);
    }

    @Test
    public void hammingDistance2() {
        final ObjectUtils ou = ObjectUtils.build();
        final List<Integer> l1 = Arrays.asList(1, 2, 3, 4);
        final List<Integer> l2 = Arrays.asList(1, 2, 3, 4);
        assertEquals(0D, ou.extendedHammingDistance(ou.makeSerializable(l1), ou.makeSerializable(l2)), 1e-5);
        l1.set(1, 5);
        assertEquals(1D, ou.extendedHammingDistance(ou.makeSerializable(l1), ou.makeSerializable(l2)), 1e-5);
        l1.set(2, 5);
        assertEquals(2D, ou.extendedHammingDistance(ou.makeSerializable(l1), ou.makeSerializable(l2)), 1e-5);
    }
}
