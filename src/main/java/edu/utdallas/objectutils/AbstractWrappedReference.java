package edu.utdallas.objectutils;

import static edu.utdallas.objectutils.Commons.newAddress;

/**
 * Base class for all non-primitive objects: arrays and proper objects
 *
 * @author Ali Ghanbari
 */
public abstract class AbstractWrappedReference implements Wrapped {
    private static final long serialVersionUID = 1L;

    protected final int address;

    protected AbstractWrappedReference() {
        this.address = newAddress();
    }

    @Override
    public int getAddress() {
        return this.address;
    }
}
