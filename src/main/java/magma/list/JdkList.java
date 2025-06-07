package magma.list;

import java.util.ArrayList;
import java.util.List;

/** Default ListLike backed by java.util.ArrayList. */
public class JdkList<T> implements ListLike<T> {
    private final List<T> list;

    private JdkList(List<T> list) {
        this.list = list;
    }

    /** Create an empty list. */
    public static <T> JdkList<T> create() {
        return new JdkList<>(new ArrayList<>());
    }

    /** Wrap an existing java.util.List. */
    public static <T> JdkList<T> wrap(List<T> list) {
        return new JdkList<>(list);
    }

    @Override
    public void add(T value) {
        list.add(value);
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public void set(int index, T value) {
        list.set(index, value);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public ListIter<T> iterator() {
        java.util.Iterator<T> it = list.iterator();
        return new ListIter<T>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }
        };
    }
}
