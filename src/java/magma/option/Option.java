package magma.option;

import java.util.function.Consumer;

public interface Option<Value> {
    void ifPresent(Consumer<Value> consumer);

    Value orElse(Value other);
}
