package magma.list;

import java.util.Iterator;
import java.util.Set;

/** Iterator backed by a java.util.Set. */
public class SetIter<T> implements Iter<T> {
    private final Iterator<T> iterator;

    private SetIter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    /** Wrap an existing Set. */
    public static <T> SetIter<T> wrap(Set<T> set) {
        return new SetIter<>(set.iterator());
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }
}
