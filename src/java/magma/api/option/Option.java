package magma.api.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Option<Value> permits None, Some {
    void ifPresent(Consumer<Value> consumer);

    Value orElse(Value other);

    <Return> Option<Return> map(Function<Value, Return> mapper);

    Value orElseGet(Supplier<Value> other);

    Option<Value> filter(Predicate<Value> predicate);

    boolean isPresent();
}
