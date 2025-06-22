package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Option<Value> {
    void ifPresent(Consumer<Value> consumer);

    Value orElse(Value other);

    <Return> Option<Return> map(Function<Value, Return> mapper);

    Value orElseGet(Supplier<Value> other);
}
