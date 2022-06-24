package edu.iastate.objectutils;

class EnumProxy implements TerminalProxy {
    private static final long serialVersionUID = 1L;

    final String typeName;

    final String constName;

    public EnumProxy(final Enum<?> val) {
        this.typeName = val.getClass().getName();
        this.constName = val.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnumProxy)) {
            return false;
        }
        final EnumProxy that = (EnumProxy) o;
        return this.typeName.equals(that.typeName)
                && this.constName.equals(that.constName);
    }

    @Override
    public String print() {
        return String.format("enum{%s:%s}", this.constName, this.typeName);
    }
}
