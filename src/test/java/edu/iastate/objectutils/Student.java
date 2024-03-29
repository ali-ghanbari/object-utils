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