package edu.utdallas.objectutils;

import java.lang.reflect.Field;

public abstract class InclusionPredicate {
    public static final InclusionPredicate INCLUDE_ALL = new InclusionPredicate() {
        @Override
        public boolean test(Field field) {
            return true;
        }
    };

    public static final InclusionPredicate INCLUDE_NONE = new InclusionPredicate() {
        @Override
        public boolean test(Field field) {
            return false;
        }
    };

    /**
     * This check should return <code>true</code> if and only if the field <code>field</code> is to be included
     * in the wrapped representation of the object. The field might be declared or inherited, or it might have
     * any access modifiers. The way the client program wants to answer is left open; one could use ASM, Soot,
     * and Java reflection to answer this question.
     *
     * @param field the field that is queried for inclusion
     * @return <code>true</code> iff <code>field</code> should be included in the wrapped representation of the
     * object
     */
    public abstract boolean test(Field field);
}
