package edu.utdallas.objectutils;

import edu.utdallas.objectutils.utils.ObjectPrinter;
import edu.utdallas.objectutils.utils.W;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static edu.utdallas.objectutils.ModificationPredicate.NO;

public abstract class AbstractWrappedObject implements Wrapped {
    protected final int address;

    protected Class<?> type; // array element type or the object type

    protected Wrapped[] values; // field values or array elements

    /* we are using this to resolve cyclic pointers between objects */
    /* is to be reset before each unwrapping operation */
    private static final Map<W, List<UnwrappedPlaceholder>> todos;

    /* we are using this to avoid re-unwrapping already unwrapped objects.
    please note that this is an important requirement for correctness of unwrapped objects. */
    /* is to be reset before each wrapping operation */
    private static final Map<W, Object> cache;

    static {
        todos = new HashMap<>();
        cache = new HashMap<>();
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

    private static Object getCache(final Wrapped wrapped) {
        return cache.get(W.of(wrapped));
    }

    private static void putCache(final Wrapped wrapped, final Object unwrapped) {
        cache.put(W.of(wrapped), unwrapped);
    }

    private static List<UnwrappedPlaceholder> getToDo(final Wrapped wrapped) {
        return todos.get(W.of(wrapped));
    }

    private static List<UnwrappedPlaceholder> createToDo(final Wrapped wrapped) {
        final List<UnwrappedPlaceholder> todoList = new LinkedList<>();
        todos.put(W.of(wrapped), todoList);
        return todoList;
    }

    private static void deleteToDo(final Wrapped wrapped) {
        todos.remove(W.of(wrapped));
    }

    @Override
    public Object unwrap() throws Exception {
        return unwrap(NO);
    }

    @Override
    public Object unwrap(ModificationPredicate shouldMutate) throws Exception {
        cache.clear();
        todos.clear();
        return unwrap0(shouldMutate);
    }

    protected abstract Object createRawObject();

    protected abstract void resetCursor(); // objects create iterator; arrays just reset to -1

    protected abstract void advanceCursor();

    protected abstract boolean strictlyImmutableAtCursor();

    protected abstract boolean shouldMutateAtCursor(ModificationPredicate mutateStatics);

    protected abstract void setAtCursor(Object rawObject, Object value) throws Exception;

    protected abstract UnwrappedPlaceholder createUnwrappedPlaceholderForCursor(final Object unwrapped);

    /* this method intended to avoid resetting cache and todos */
    private static Object unwrapMux(final Wrapped wrapped,
                                    final ModificationPredicate shouldMutate)
            throws Exception {
        if (wrapped instanceof AbstractWrappedObject) {
            return ((AbstractWrappedObject) wrapped).unwrap0(shouldMutate);
        }
        return wrapped.unwrap(shouldMutate);
    }

    private Object unwrap0(ModificationPredicate shouldMutate) throws Exception {
        final List<UnwrappedPlaceholder> todoList = createToDo(this);
        final Object unwrapped = createRawObject();
        resetCursor();
        for (final Wrapped wrappedValue : this.values) {
            advanceCursor();
            while (strictlyImmutableAtCursor()) {
                advanceCursor();
            }
            if (shouldMutateAtCursor(shouldMutate)) {
                Object value = null;
                if (wrappedValue != null) {
                    final List<UnwrappedPlaceholder> targetObjectToDoList =
                            getToDo(wrappedValue);
                    if (targetObjectToDoList != null) { // cycle?
                        final UnwrappedPlaceholder placeholder =
                                createUnwrappedPlaceholderForCursor(unwrapped);
                        targetObjectToDoList.add(placeholder);
                    } else {
                        value = getCache(wrappedValue);
                        if (value == null) {
                            value = unwrapMux(wrappedValue, shouldMutate);
                            //putCache(wrappedValue, value);
                        }
                    }
                }
                setAtCursor(unwrapped, value);
            }
        }
        putCache(this, unwrapped);
        deleteToDo(this);
        for (final UnwrappedPlaceholder placeholder : todoList) {
            placeholder.substitute(unwrapped);
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
