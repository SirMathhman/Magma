package magma;

/**
 * Strategy for collecting elements from an {@link Iterator} into a container.
 */
interface Collector<T, C> {
    C createInitial();

    C fold(C current, T element);
}
