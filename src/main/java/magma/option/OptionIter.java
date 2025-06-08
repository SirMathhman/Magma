package magma.option;

import magma.list.Iter;

public class OptionIter<T> implements Iter<T> {
    private final Option<T> self;
    private boolean done;

    public OptionIter(Option<T> self) {
        this.self = self;
        done = !self.isSome();
    }

    @Override
    public boolean hasNext() {
        return !done;
    }

    @Override
    public T next() {
        done = true;
        return self.get();
    }
}
