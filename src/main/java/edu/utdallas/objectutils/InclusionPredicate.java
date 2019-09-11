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
     * This check should return <code>true</code> if and only if <code>field</code> is to be included
     * in the wrapped representation of the object. Because of the complexities of dealing with field type
     * and declaring/inheriting class, I expect that any implementation of this method will check only the
     * type of the field: the method will return true iff the type is primitive and it is accessed
     * (read or updated) in the body of the method to be memoized. However, later, we might come up with more
     * advanced, admissive analyzes that let us include more fields.
     *
     * @param field the field that is queried for inclusion
     * @return <code>true</code> iff <code>field</code> should be included in the wrapped representation of the
     * object
     */
    public abstract boolean test(Field field);
}
