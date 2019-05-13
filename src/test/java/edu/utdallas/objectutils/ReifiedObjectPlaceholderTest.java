package edu.utdallas.objectutils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Ali Ghanbari
 */
public class ReifiedObjectPlaceholderTest {
    private static class A {
        private final int fieldA = 1;
        public static String fieldB = "Hello";
        private static final int fieldC = 10;
        public static final String fieldD = "World";
        private String fieldE = "!";

        @Override
        public String toString() {
            return String.format("A{FieldA=%d,fieldB=\"%s\",fieldC=%d,fieldD=\"%s\",fieldE=\"%s\"}",
                    this.fieldA,
                    fieldB,
                    fieldC,
                    fieldD,
                    this.fieldE);
        }
    }

    @Test
    public void substitute() throws Exception {
        final A a = new A();
        assertEquals("A{FieldA=1,fieldB=\"Hello\",fieldC=10,fieldD=\"World\",fieldE=\"!\"}", a.toString());
        final ReifiedObjectPlaceholder ph = new ReifiedObjectPlaceholder(a, 4);
        ph.substitute("*");
        assertEquals("A{FieldA=1,fieldB=\"Hello\",fieldC=10,fieldD=\"World\",fieldE=\"*\"}", a.toString());
    }
}