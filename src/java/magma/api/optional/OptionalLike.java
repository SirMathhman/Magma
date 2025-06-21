package magma.api.optional;

import java.util.function.Consumer;
import java.util.function.Function;

public interface OptionalLike<Value> {
    <Return> OptionalLike<Return> map(Function<Value, Return> mapper);

    void ifPresent(Consumer<Value> consumer);

    Value orElse(Value other);
}
