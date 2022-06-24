package edu.iastate.objectutils;

class Student extends Person {
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