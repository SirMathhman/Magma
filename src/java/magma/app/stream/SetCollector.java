package magma.app.stream;

import java.util.HashSet;
import java.util.Set;

public class SetCollector<T> implements CollectorLike<T, Set<T>> {
    @Override
    public Set<T> createInitial() {
        return new HashSet<>();
    }

    @Override
    public Set<T> fold(final Set<T> current, final T t) {
        current.add(t);
        return current;
    }
}
