import ArrayList from "../../java/util/ArrayList";
import List from "../../java/util/List";
/** Default ListLike backed by java.util.ArrayList. */
export default class JdkList<T> implements ListLike<T> {
    private readonly list: List<T>;

    JdkList(list: List<T>): private {
        // TODO
    }

    /** Create an empty list. */
    create(): JdkList<T> {
        return new JdkList<T>(new ArrayList<T>());
    }

    /** Wrap an existing java.util.List. */
    wrap(list: List<T>): JdkList<T> {
        return new JdkList<T>(list);
    }

    @Override
    add(value: T): void {
        list.add(value);
    }

    @Override
    get(index: number): T {
        return list.get(index);
    }

    @Override
    set(index: number, value: T): void {
        list.set(index, value);
    }

    @Override
    size(): number {
        return list.size();
    }

    @Override
    iterator(): ListIter<T> {
        let it : java.util.Iterator<T> = list.iterator();
        return new ListIter<T>();
        // TODO
        public boolean hasNext();
        return it.hasNext();
        // TODO
        // TODO
        public T next();
        return it.next();
        // TODO
        // TODO
    }
}
