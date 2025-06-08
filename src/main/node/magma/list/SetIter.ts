import Iterator from "../../java/util/Iterator";
import Set from "../../java/util/Set";
/** Iterator backed by a java.util.Set. */
export default class SetIter<T> implements Iter<T> {
    private readonly iterator: Iterator<T>;

    SetIter(iterator: Iterator<T>): private {
        // TODO
    }

    /** Wrap an existing Set. */
    wrap(set: Set<T>): SetIter<T> {
        return new SetIter<T>(set.iterator());
    }

    @Override
    hasNext(): boolean {
        return iterator.hasNext();
    }

    @Override
    next(): T {
        return iterator.next();
    }
}
