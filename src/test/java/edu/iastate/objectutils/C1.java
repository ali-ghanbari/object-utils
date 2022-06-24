package edu.iastate.objectutils;

import java.util.Arrays;
import java.util.List;

class C1 extends B {
        final int f1;
        final String f2;

        private final List<B> fl;

        public C1(double f, int f1, String f2) {
            super(f);
            this.f1 = f1;
            this.f2 = f2;
            final C1[] o = new C1[1];
            o[0] = this;
            this.fl = Arrays.asList(this, null, new B(2), new C2(0.001, 7, o));
        }
    }