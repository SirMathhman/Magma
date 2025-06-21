package magma.api.optional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OptionalLike<Value> {
    <Return> OptionalLike<Return> map(Function<Value, Return> mapper);

    void ifPresent(Consumer<Value> consumer);

    Value orElseGet(Supplier<Value> supplier);

    Value orElse(Value other);
}
