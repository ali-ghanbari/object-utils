package edu.utdallas.objectutils;

public enum WrappedNull implements Wrapped {
    INSTANCE;

    private static final long serialVersionUID = 1L;

    @Override
    public <T> T unwrap() throws Exception {
        return null;
    }

    @Override
    public <T> T unwrap(ModificationPredicate shouldMutate) throws Exception {
        return null;
    }

    @Override
    public int getAddress() {
        return 0;
    }

    @Override
    public String print() {
        return "null";
    }
}
