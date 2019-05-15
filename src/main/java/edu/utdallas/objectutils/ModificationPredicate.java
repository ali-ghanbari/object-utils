package edu.utdallas.objectutils;

public abstract class ModificationPredicate {
    public static final ModificationPredicate YES = new ModificationPredicate() {
        @Override
        public boolean test(final Class<?> object) {
            return true;
        }
    };

    public static final ModificationPredicate NO = new ModificationPredicate() {
        @Override
        public boolean test(final Class<?> object) {
            return false;
        }
    };

    public abstract boolean test(Class<?> object);
}
