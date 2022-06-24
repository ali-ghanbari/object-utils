package edu.iastate.objectutils;

class W<T> {
    final T value;

    final int id;

    W(final T value) {
        this.id = System.identityHashCode(value);
        this.value = value;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof W)) {
            return false;
        }
        final W<?> that = (W<?>) obj;
        if (this.id != that.id) {
            return false;
        }
        return this.value == that.value;
    }
}
