package magma.build.java;

import magma.api.contain.stream.Stream;

public interface Set<T> {
    Set<T> add(T next);

    Stream<T> stream();
}
