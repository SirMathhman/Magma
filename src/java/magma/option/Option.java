package magma.option;

import java.util.function.Consumer;

public interface Option<T> {
    void ifPresent(Consumer<T> consumer);
}
