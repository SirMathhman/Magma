package magma.option;

/**
 * Minimal optional value container with distinct variants.
 */
public interface Option<T> {
    boolean isSome();

    T get();

    default magma.list.Iter<T> toIter() {
        Option<T> self = this;
        return new magma.list.Iter<T>() {
            private boolean done = !self.isSome();

            @Override
            public boolean hasNext() {
                return !done;
            }

            @Override
            public T next() {
                done = true;
                return self.get();
            }
        };
    }
}
