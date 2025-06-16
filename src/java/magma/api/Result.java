package magma.api;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<Value, Error> {
    <Return> Result<Return, Error> flatMap(Function<Value, Result<Return, Error>> mapper);

    <Return> Result<Return, Error> map(Function<Value, Return> mapper);

    Optional<Value> findValue();

    void consume(Consumer<Value> whenOk, Consumer<Error> whenErr);
}