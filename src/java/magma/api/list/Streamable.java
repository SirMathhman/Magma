package magma.api.list;

import java.util.stream.Stream;

public interface Streamable<T> {
    Stream<T> stream();
}
