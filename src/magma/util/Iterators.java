package magma.util;

public class Iterators {
    public static <T> Iterator<T> fromOptional(Option<T> option) {
        return new HeadedIterator<>(option
                .<Head<T>>map(SingleHead::new)
                .orElseGet(EmptyHead::new));
    }

    public static <T> Iterator<T> empty() {
        return new HeadedIterator<>(new EmptyHead<>());
    }
}
