package edu.utdallas.objectutils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static edu.utdallas.objectutils.ModificationPredicate.NO;

public abstract class AbstractWrappedObject implements Wrapped {
    protected final int address;

    protected Class<?> type; // array element type or the object type

    protected Wrapped[] values; // field values or array elements

    /* We are using this to unwrap cyclic object graphs */
    /* We consult this hash-table to find out if we have already unwrapped a wrapped object */
    /* Note that we use object addresses as key for performance reasons */
    private static final Map<Integer, Object> unwrappedObjects;

    static {
        unwrappedObjects = new HashMap<>();
    }

    AbstractWrappedObject(Class<?> type, Wrapped[] values) {
        this.values = values;
        this.type = type;
        this.address = Commons.newAddress();
    }

    /**
     * Returns the type of array elements, in case <code>this</code>
     * represents an object array, or simply object type that is wrapped
     * by <code>this</code>.
     *
     * @return Type
     */
    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * Returns the values of array elements, in case <code>this</code>
     * represents an object array, or field values of the object that
     * is wrapped by <code>this</code>.
     * @return Field values or array element values
     */
    public Wrapped[] getValues() {
        return values;
    }

    public void setValues(Wrapped[] values) {
        this.values = values;
    }

    @Override
    public Object unwrap() throws Exception {
        return unwrap(NO);
    }

    @Override
    public Object unwrap(ModificationPredicate shouldMutate) throws Exception {
        unwrappedObjects.clear();
        return unwrap0(shouldMutate);
    }

    protected abstract Object createRawObject();

    protected abstract void resetCursor(); // objects create iterator; arrays just reset to -1

    protected abstract void advanceCursor();

    protected abstract boolean strictlyImmutableAtCursor();

    protected abstract boolean shouldMutateAtCursor(ModificationPredicate mutateStatics);

    protected abstract void setAtCursor(Object rawObject, Object value) throws Exception;

    private Object unwrap0(ModificationPredicate shouldMutate) throws Exception {
        final Object unwrapped = createRawObject();
        unwrappedObjects.put(this.address, unwrapped);
        resetCursor();
        for (final Wrapped wrappedValue : this.values) {
            advanceCursor();
            while (strictlyImmutableAtCursor()) {
                advanceCursor();
            }
            if (shouldMutateAtCursor(shouldMutate)) {
                Object value = null;
                if (wrappedValue != WrappedNull.INSTANCE) {
                    if (wrappedValue instanceof AbstractWrappedObject) {
                        final AbstractWrappedObject wrappedObject = (AbstractWrappedObject) wrappedValue;
                        final Object targetObject = unwrappedObjects.get(wrappedObject.address);
                        if (targetObject != null) { // cycle?
                            value = targetObject;
                        } else {
                            value = wrappedObject.unwrap0(shouldMutate);
                        }
                    } else {
                        value = wrappedValue.unwrap(shouldMutate);
                    }
                }
                setAtCursor(unwrapped, value);
            }
        }
        return unwrapped;
    }

    @Override
    public int getAddress() {
        return this.address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }
}
