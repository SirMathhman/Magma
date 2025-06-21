package magma.app.list;

import java.util.stream.Stream;

public interface ListLike<T> {
    static <T> ListLike<T> empty() {
        return new JavaList<>();
    }

    Stream<T> stream();

    ListLike<T> add(T element);
}
