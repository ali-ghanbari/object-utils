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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
}
