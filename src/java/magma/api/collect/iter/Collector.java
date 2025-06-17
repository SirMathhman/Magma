package magma.api.collect.iter;

public interface Collector<T, C> {
    C createInitial();

    C fold(C current, T element);
}
