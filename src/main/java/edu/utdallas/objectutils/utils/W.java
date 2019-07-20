package edu.utdallas.objectutils.utils;

import java.util.Objects;

public class W {
    private final Object core;

    private W(Object core) {
        this.core = core;
    }

    public static W of(final Object core) {
        return new W(core);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this.core == o) {
//            return true;
//        }
//        if (o == null) {
//            return false;
//        }
//        if (this.core.getClass() != o.getClass()) {
//            return false;
//        }
//        return Objects.equals(this.core, o);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(this.core);
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        W w = (W) o;
        return this.core == w.core;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.core);
    }
}
