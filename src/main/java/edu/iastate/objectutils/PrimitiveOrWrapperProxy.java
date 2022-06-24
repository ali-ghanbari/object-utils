package edu.iastate.objectutils;

class PrimitiveOrWrapperProxy implements TerminalProxy {
    private static final long serialVersionUID = 1L;

    final Object obj;

    public PrimitiveOrWrapperProxy(final Object obj) {
        this.obj = obj;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrimitiveOrWrapperProxy)) {
            return false;
        }
        final PrimitiveOrWrapperProxy that = (PrimitiveOrWrapperProxy) o;
        return this.obj.equals(that.obj);
    }

    @Override
    public String print() {
        return obj.toString();
    }
}
