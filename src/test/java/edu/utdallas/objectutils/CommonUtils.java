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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


class CommonUtils {
    static Object[] generateAVeryComplexObject() {
        final Object[] objects = new Object[8];
        final StringBuilder sb = new StringBuilder("ali");
        final List<Integer> list1 = new ArrayList<>();
        final Set<String> set1 = new HashSet<>();
        final Map<Integer, Double> map = new LinkedHashMap<>();
        for (int i = 0; i < 1000; i++) {
            sb.append('.').append(i).append(';');
            list1.add(i);
            set1.add(sb.toString());
            map.put(i, Math.sin(i));
        }
        objects[0] = sb.toString();
        final Object da = objects[1] = new double[] {
                0D,
                Math.log(3.14D) * sb.length(),
                Math.log1p(2.718D) * sb.length(),
                Math.sinh(sb.length()) * Math.sinh(22D),
                Math.cosh(sb.length()) + Math.cosh(sb.length())
        };
        objects[2] = new Object[] {null, String.class, objects, da, sb, sb.getClass()};
        objects[3] = objects;
        objects[4] = null;
        final ObjectUtilsTest.Record r1 =
                new ObjectUtilsTest.Record("r1", "30");
        final ObjectUtilsTest.Record r2 =
                new ObjectUtilsTest.Record("r2", "40");
        final ObjectUtilsTest.Record r3 =
                new ObjectUtilsTest.Record("r2", "30");
        final ObjectUtilsTest.Student2 s1 =
                new ObjectUtilsTest.Student2("Ali", 28, r1, r2);
        final Object[] c = new Object[1];
        c[0] = c;
        final ObjectUtilsTest.Student2 s2 = new ObjectUtilsTest.Student2("TheOtherStudent", 28, r1, r3);
        objects[5] = new Object[] {c, ObjectUtilsTest.Colors.GREEN, s1, s2, list1, set1, map, c};
        objects[6] = Collections.singletonMap(0, objects);
        final Hashtable<Integer, Object> ht = new Hashtable<>();
        final List<Object> list2 = new LinkedList<>();
        list2.add(s1); list2.add(s2);
        list2.add(r1); list2.add(r2);
        list2.add(c); list2.add(map);
        ht.put(0, list2); list2.add(ht);
        ht.put(list1.getClass().hashCode(), "hello");
        ht.put(list2.getClass().hashCode(), "world");
        ht.put(ht.getClass().hashCode(), "how");
        objects[7] = new Object[] {ht, list2};
        return objects;
    }
}
