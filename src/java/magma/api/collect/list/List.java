package magma.api.collect.list;

import magma.api.collect.iter.Iterable;

public interface List<T> extends Iterable<T> {
    List<T> add(T element);
}
