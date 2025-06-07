package magma.util;

/**
 * Strategy for collecting elements from an {@link Iterator} into a container.
 */
public interface Collector<T, C> {
    C createInitial();

    C fold(C current, T element);
}
