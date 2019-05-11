package edu.utdallas.objectutils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class WrapperTest {

    @Test
    public void wrapBoolean() throws Exception {
        Wrapped wrapped = Wrapper.wrapBoolean(true);
        assertTrue((Boolean) wrapped.reconstruct());
        wrapped = Wrapper.wrapBoolean(false);
        assertFalse((Boolean) wrapped.reconstruct(true));
    }

    @Test
    public void wrapByte() throws Exception {
        Wrapped wrapped = Wrapper.wrapByte((byte) 10);
        assertEquals(10, ((Byte) wrapped.reconstruct()).intValue());
        wrapped = Wrapper.wrapByte((byte) 20);
        assertEquals(20, ((Byte) wrapped.reconstruct(true)).intValue());
    }

    @Test
    public void wrapChar() throws Exception {
        Wrapped wrapped = Wrapper.wrapChar('a');
        assertEquals((int) 'a', ((Character) wrapped.reconstruct()).charValue());
        wrapped = Wrapper.wrapChar('b');
        assertEquals((int) 'b', ((Character) wrapped.reconstruct(true)).charValue());
    }

    @Test
    public void wrapDouble() throws Exception {
        Wrapped wrapped = Wrapper.wrapDouble(10.D);
        assertEquals(10.D, (Double) wrapped.reconstruct(), 0.0001D);
        wrapped = Wrapper.wrapDouble(11.D);
        assertEquals(11.D, (Double) wrapped.reconstruct(true), 0.0001D);
    }

    @Test
    public void wrapFloat() throws Exception {
        Wrapped wrapped = Wrapper.wrapFloat(10.F);
        assertEquals(10.F, (Float) wrapped.reconstruct(), 0.0001F);
        wrapped = Wrapper.wrapFloat(11.F);
        assertEquals(11.F, (Float) wrapped.reconstruct(true), 0.0001F);
    }

    @Test
    public void wrapInt() throws Exception {
        Wrapped wrapped = Wrapper.wrapInt(10);
        assertEquals(10, ((Integer) wrapped.reconstruct()).intValue());
        wrapped = Wrapper.wrapInt(20);
        assertEquals(20, ((Integer) wrapped.reconstruct(true)).intValue());
    }

    @Test
    public void wrapLong() throws Exception {
        Wrapped wrapped = Wrapper.wrapLong(10L);
        assertEquals(10L, ((Long) wrapped.reconstruct()).longValue());
        wrapped = Wrapper.wrapLong(20L);
        assertEquals(20L, ((Long) wrapped.reconstruct(true)).longValue());
    }

    @Test
    public void wrapShort() throws Exception {
        Wrapped wrapped = Wrapper.wrapShort((short) 10);
        assertEquals(10, ((Short) wrapped.reconstruct()).shortValue());
        wrapped = Wrapper.wrapShort((short) 20);
        assertEquals(20, ((Short) wrapped.reconstruct(true)).shortValue());
    }

    @Test
    public void wrapString() throws Exception {
        Wrapped wrapped = Wrapper.wrapString("hello");
        assertEquals("hello", wrapped.reconstruct());
        wrapped = Wrapper.wrapString("world");
        assertEquals("world", wrapped.reconstruct(true));
    }

    private static class A {
        public static final String AX = "10";
        private static int _AX = 10;
        private final int ax;
        private int ah;
        protected int al;

        A(int ax, int ah, int al) {
            this.ax = ax;
            this.ah = ah;
            this.al = al;
        }

        public int getAx() {
            return ax;
        }

        public int getAh() {
            return ah;
        }

        public int getAl() {
            return al;
        }

        public boolean checkRep() {
            return _AX == 10;
        }
    }

    private String myExternalField = "HELLO!";

    private class B extends A {
        public final String name = "BX";
        private final int value;
        final R r;

        B(int value) {
            super(10, 10, 10);
            this.value = value;
            this.r = new R("bx", 10);
        }

        public int getValue() {
            return value;
        }

        public R getR() {
            return r;
        }

        public String getExternalField() {
            return myExternalField;
        }
    }

    private final class R {
        private final String name;
        private final int val;

        R(String name, int val) {
            this.name = name;
            this.val = val;
        }

        public String getName() {
            return name;
        }

        public int getVal() {
            return val;
        }
    }

    @Test
    public void wrapObject() throws Exception {
        final B b = new B(30);
        checkRep(b);
        Wrapped wrapped = Wrapper.wrapObject(b);
        final B bPrime = wrapped.reconstruct();
        checkRep(b);
        checkRep(bPrime);
        assertEquals("HELLO!", bPrime.getExternalField());
        this.myExternalField = "WORLD!";
        wrapped = Wrapper.wrapObject(b);
        final B bDoublePrime = wrapped.reconstruct(true);
        checkRep(b);
        checkRep(bPrime);
        checkRep(bDoublePrime);
        assertEquals("WORLD!", bDoublePrime.getExternalField());
    }

    private void checkRep(final B b) {
        final R r = b.r;
        if (r.name.equals("bx") && r.val == 10) {
            if (b.value == 30) {
                if (b.name.equals("BX")) {
                    if (b.getAh() == 10 && b.getAl() == 10 && b.getAx() == 10) {
                        if (b.checkRep()) {
                            if (!B.AX.equals("10")) {
                                throw new IllegalStateException("bad AX");
                            }
                        } else {
                            throw new IllegalStateException("bad _AX");
                        }
                    } else {
                        throw new IllegalStateException("bad ah | al | ax");
                    }
                } else {
                    throw new IllegalStateException("bad name");
                }
            } else {
                throw new IllegalStateException("bad value");
            }
        } else {
            throw new IllegalStateException("bad r");
        }
    }

    private static class Record {
        private final String recordId;
        private String recordValue;

        public Record(String recordId, String recordValue) {
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

        public Records(final Record... records) {
            this.records = Arrays.asList(records);
        }
    }

    private static class Student {
        private final String name;
        private final int age;
        private final Records records;

        public Student(String name, int age, final Record... records) {
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
    public void wrapObject1() throws Exception {
        final Record r1 = new Record("r1", "30");
        final Record r2 = new Record("r2", "40");
        final Student s1 = new Student("Ali", 28, r1, r2);
        final Student s2 = new Student("Ali", 28, r1, r2);
        assertNotEquals(s1, s2);
        final Wrapped w1 = Wrapper.wrapObject(s1);
        final Wrapped w2 = Wrapper.wrapObject(s2);
        assertEquals(w1, w2);
    }

}