package edu.utdallas.objectutils;

/*
 * #%L
 * object-utils
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

import org.junit.Assert;
import org.junit.Test;

public class TemplateBasedWrapperTest {
    private static class Person {
        String name;
        int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    @Test
    public void tempWrapperTest1() throws Exception {
        final Object[][] a = {
                new Object[]{new Person("a", 28)},
                new Object[] {new Person("b", 29)}
        };
        final Wrapped wrappedA = Wrapper.wrapObject(a);
        final Person p = (Person) a[1][0];
        p.name = "k";
        final String s1 = p.toString();
        wrappedA.unwrap(a);
        Assert.assertNotEquals(s1, p.toString());
    }
}
