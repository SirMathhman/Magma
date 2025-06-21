package magma.api.optional;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Option<Value> {
    <Return> Option<Return> map(Function<Value, Return> mapper);

    void ifPresent(Consumer<Value> consumer);

    Value orElse(Value other);
}
