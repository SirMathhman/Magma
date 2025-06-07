package magma.list;

/** Minimal iterator abstraction independent of java.util.Iterator. */
public interface ListIterator<T> {
    boolean hasNext();
    T next();
}
