package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Option<Value> {
    void ifPresent(Consumer<Value> consumer);

    Value orElse(Value other);

    <Return> Option<Return> map(Function<Value, Return> mapper);

    <Return> Option<Return> flatMap(Function<Value, Option<Return>> mapper);

    Value orElseGet(Supplier<Value> other);
}
