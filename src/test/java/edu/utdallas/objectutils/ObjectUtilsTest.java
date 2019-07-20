package edu.utdallas.objectutils;

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
