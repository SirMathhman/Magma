package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Option<Value> {
    void ifPresent(Consumer<Value> consumer);

    Value orElse(Value other);

    <Return> Option<Return> flatMap(Function<Value, Option<Return>> mapper);
}
