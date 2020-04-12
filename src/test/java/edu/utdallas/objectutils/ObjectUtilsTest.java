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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author Ali Ghanbari
 */
public class ObjectUtilsTest {
	private static class Person {
		protected final String name;
		protected final String surname;

		public Person(String name, String surname) {
			this.name = name;
			this.surname = surname;
		}
	}

	private static class Student extends Person {
		private static int count = 0;
		private double GPA;

		public Student(String name, String surname, double GPA) {
			super(name, surname);
			this.GPA = GPA;
		}

		@Override
		public String toString() {
			return "Student [GPA=" + GPA + ", name=" + name + ", surname=" + surname + "]";
		}
	}

	@Test
	public void testShallowCopy1() {
		final Student s1 = new Student("a", "b", 3.D);
		final Student s2 = new Student("c", "d", 4.D);
		try {
			ObjectUtils.shallowCopy(s1, s2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Student [GPA=4.0, name=c, surname=d]", s1.toString());
	}

	@Test
	public void testDeepHashCodeBasics1() throws Exception {
		final Object[] objects = {
				1,
				2.718D,
				4L,
				40.21F,
				null
		};
		for (int i = 0; i < objects.length; i++) {
			final Object object = objects[i];
			final long expected = Objects.hashCode(object);
			final long actual = ObjectUtils.deepHashCode(object);
//			System.out.printf("<%d, %d>%n", expected, actual);
			final String message = String.format("object at %d: expected <%d> but was <%d>", i, expected, actual);
			assertEquals(message, expected, actual);
		}
	}

	@Test
	public void testDeepHashCodeBasics2() throws Exception {
		final String[] parts = {
				"h",
				"ello",
				"he",
				"llo"
		};
		final String string1 = parts[0] + parts[1];
		final String string2 = parts[2] + parts[3];
		assertNotSame(string1, string2);
//		System.out.printf("<%d, %d>%n", ObjectUtils.deepHashCode(string1), ObjectUtils.deepHashCode(string2));
		assertEquals(ObjectUtils.deepHashCode(string1), ObjectUtils.deepHashCode(string2));
	}

	@Test
	public void testDeepHashCodeBasics3() throws Exception {
		final StringBuilder sb1 = new StringBuilder("bye");
		final StringBuilder sb2 = new StringBuilder("hello");
		sb2.delete(0, 2);
		sb2.setCharAt(0, 'b');
		sb2.setCharAt(1, 'y');
		sb2.setCharAt(2, 'e');
//		System.out.printf("<%d, %d>%n", ObjectUtils.deepHashCode(sb1.toString()), ObjectUtils.deepHashCode(sb2.toString()));
		assertEquals(ObjectUtils.deepHashCode(sb1.toString()), ObjectUtils.deepHashCode(sb2.toString()));
	}

	private enum Colors {
		RED,
		GREEN;
	}

	@Test
	public void testDeepHashCodeEnums() throws Exception {
//		System.out.printf("<%d, %d>%n", ObjectUtils.deepHashCode(Colors.GREEN), ObjectUtils.deepHashCode(Colors.GREEN));
		assertEquals(ObjectUtils.deepHashCode(Colors.GREEN), ObjectUtils.deepHashCode(Colors.GREEN));
		assertEquals(ObjectUtils.deepHashCode(Colors.RED), ObjectUtils.deepHashCode(Colors.RED));
		assertNotEquals(ObjectUtils.deepHashCode(Colors.RED), ObjectUtils.deepHashCode(Colors.GREEN));
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

	private static class Student2 {
		private final String name;
		private final int age;
		private final Records records;

		Student2(String name, int age, final Record... records) {
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
	public void testDeepHashCodeCompositeObject1() throws Exception {
		final Record r1 = new Record("r1", "30");
		final Record r2 = new Record("r2", "40");
		final Record r3 = new Record("r2", "30");
		final Student2 s1 = new Student2("Ali", 28, r1, r2);
		final Student2 s2 = new Student2("Ali", 28, r1, r3);
		assertNotEquals(ObjectUtils.deepHashCode(s1), ObjectUtils.deepHashCode(s2));
		final StringBuilder sb = new StringBuilder();
		sb.append('0');
		sb.append('4');
		r3.setRecordValue(sb.reverse().toString());
//		System.out.printf("<%d, %d>%n", ObjectUtils.deepHashCode(s1), ObjectUtils.deepHashCode(s2));
		assertEquals(ObjectUtils.deepHashCode(s1), ObjectUtils.deepHashCode(s2));
	}

	@Test
	public void testDeepHashCodeCompositeObject2() throws Exception {
		final List<String> l1 = new LinkedList<>();
		l1.add("1");
		l1.add("2");
		l1.add("3");
		l1.add("4");
		assertEquals(ObjectUtils.deepHashCode(l1), ObjectUtils.deepHashCode(l1));
		final List<String> l2 = new LinkedList<>();
		l2.add("1");
		l2.add("2");
		l2.add("2");
		l2.add("3");
		l2.add("3");
		l2.add("4");
		assertEquals(ObjectUtils.deepHashCode(l2), ObjectUtils.deepHashCode(l2));
		l2.remove(1);
		l2.remove(2);
		assertEquals(ObjectUtils.deepHashCode(l2), ObjectUtils.deepHashCode(l2));
		for (final Field field : FieldUtils.getAllFields(LinkedList.class)) {
			if (field.getName().equals("modCount")) {
				assertNotEquals(FieldUtils.readField(field, l1, true), FieldUtils.readField(field, l2, true));
				break;
			}
		}
		assertNotEquals(Wrapper.wrapObject(l1), Wrapper.wrapObject(l2));
		assertNotEquals(ObjectUtils.deepHashCode(l1), ObjectUtils.deepHashCode(l2));
		final List<String> l3 = new LinkedList<>();
		l3.add("1");
		l3.add("2");
		l3.add("3");
		l3.add("4");
		assertEquals(ObjectUtils.deepHashCode(l1), ObjectUtils.deepHashCode(l3));
		l1.remove(0);
		l3.remove(0);
		assertEquals(ObjectUtils.deepHashCode(l1), ObjectUtils.deepHashCode(l3));
	}

	@Test
	public void testDeepHashCodeCompositeObject3() throws Exception {
		final Object[] objects1 = new Object[2];
		final Object[] objects2 = new Object[2];
		objects1[0] = objects2;
		objects1[1] = 1;
		objects2[0] = objects1;
		objects2[1] = 1;
		//  +------>(1)
		//  |        |      Are they equal?! Yes, I guess!
		// (1)<------+
		//
		assertEquals(ObjectUtils.deepHashCode(objects1), ObjectUtils.deepHashCode(objects2));
	}

	@Test
	public void testDeepHashCodeCompositeObject4() throws Exception {
		final HashSet<String> strings = new HashSet<>();
		strings.add("hello");
		strings.add("world!");
		strings.add("how");
		strings.add("are");
		strings.add("you?");
		strings.add("is");
		strings.add("everything");
		strings.add("OK?");
		strings.add("sure?!");
		final HashSet<String> strings2 = new HashSet<>();
		strings2.add("hello");
		strings2.add("world!");
		strings2.add("how");
		strings2.add("are");
		strings2.add("you?");
		strings2.add("is");
		strings2.add("everything");
		strings2.add("OK?");
		strings2.add("sure?!");
		for (int __ = 0; __ < 1000; __++) {
			assertEquals(strings.hashCode(), strings2.hashCode());
			assertEquals(ObjectUtils.deepHashCode(strings), ObjectUtils.deepHashCode(strings2));
		}
	}

	@Test
	public void testDeepHashCodeCompositeObject5() throws Exception {
		assertNotEquals(ObjectUtils.deepHashCode(String.class), ObjectUtils.deepHashCode(Integer.class));
		assertEquals(ObjectUtils.deepHashCode(String.class), ObjectUtils.deepHashCode(String.class));
	}

	private static Object[] generateAVeryComplexObject() {
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
		final Record r1 = new Record("r1", "30");
		final Record r2 = new Record("r2", "40");
		final Record r3 = new Record("r2", "30");
		final Student2 s1 = new Student2("Ali", 28, r1, r2);
		final Object[] c = new Object[1];
		c[0] = c;
		final Student2 s2 = new Student2("TheOtherStudent", 28, r1, r3);
		objects[5] = new Object[] {c, Colors.GREEN, s1, s2, list1, set1, map, c};
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

	@Test
	public void testDeepHashCodeComplexObject1() throws Exception {
		final Object i1 = generateAVeryComplexObject();
		final Object i2 = generateAVeryComplexObject();
		assertNotSame(i1, i2);
		assertEquals(Wrapper.wrapObject(i1), Wrapper.wrapObject(i2));
		assertEquals(ObjectUtils.deepHashCode(i1), ObjectUtils.deepHashCode(i2));
	}

	@Test
	public void testDeepHashCodeComplexObject2() throws Exception {
		final Object i1 = generateAVeryComplexObject();
		final Object i2 = generateAVeryComplexObject();
		assertNotSame(i1, i2);
		final Wrapped w1 = Wrapper.wrapObject(i1);
		final Wrapped w2 = Wrapper.wrapObject(i2);
		assertEquals(w1, w2);
		assertEquals(ObjectUtils.deepHashCode(i1), ObjectUtils.deepHashCode(i2));
		assertEquals(ObjectUtils.deepHashCode(w1.unwrap()), ObjectUtils.deepHashCode(w2.unwrap()));
	}

	@Test
	public void testDeepHashCodeComplexObject3() throws Exception {
		final Object[] i = {
				generateAVeryComplexObject(),
				generateAVeryComplexObject(),
				generateAVeryComplexObject(),
				generateAVeryComplexObject(),
				generateAVeryComplexObject(),
				generateAVeryComplexObject(),
				generateAVeryComplexObject(),
				generateAVeryComplexObject()
		};
		final long start = System.currentTimeMillis();
		for (int ignored = 0; ignored < 5; ignored++) {
			ObjectUtils.deepHashCode(i);
		}
		System.out.println("----5x8 complex objects----");
		System.out.println(System.currentTimeMillis() - start + " ms");
		System.out.println("---------------------------");
	}

	@Test
	public void testDeepHashCodeComplexObject4() throws Exception {
		final InclusionPredicate ip1 = new InclusionPredicate() {
			@Override
			public boolean test(Field field) {
				final String fieldName = field.getName().toLowerCase();
				return Math.max(fieldName.indexOf('r'), fieldName.indexOf('a')) >= 0;
			}
		};
		Object i1 = generateAVeryComplexObject();
		Object i2 = generateAVeryComplexObject();
		assertNotSame(i1, i2);
		final Wrapped w1 = Wrapper.wrapObject(i1, ip1);
		final Wrapped w2 = Wrapper.wrapObject(i2, ip1);
		assertEquals(w1, w2);
		assertEquals(ObjectUtils.deepHashCode(i1, ip1), ObjectUtils.deepHashCode(i2, ip1));
		i1 = generateAVeryComplexObject();
		i2 = generateAVeryComplexObject();
		assertEquals(ObjectUtils.deepHashCode(w1.unwrap(i1)), ObjectUtils.deepHashCode(w2.unwrap(i2)));
	}

	@Test
	public void testDeepHashCodeComplexObject5() throws Exception {
		final InclusionPredicate ip1 = new InclusionPredicate() {
			@Override
			public boolean test(Field field) {
				final String fieldName = field.getName().toLowerCase();
				return Math.max(fieldName.indexOf('r'), fieldName.indexOf('a')) >= 0 || fieldName.length() > 5;
			}
		};
		Object i1 = generateAVeryComplexObject();
		Object i2 = generateAVeryComplexObject();
		assertNotSame(i1, i2);
		final Wrapped w1 = Wrapper.wrapObject(i1, ip1);
		final Wrapped w2 = Wrapper.wrapObject(i2, ip1);
		assertEquals(w1, w2);
		assertEquals(ObjectUtils.deepHashCode(i1, ip1), ObjectUtils.deepHashCode(i2, ip1));
		i1 = generateAVeryComplexObject();
		i2 = generateAVeryComplexObject();
		assertEquals(ObjectUtils.deepHashCode(w1.unwrap(i1)), ObjectUtils.deepHashCode(w2.unwrap(i2)));
	}
}
