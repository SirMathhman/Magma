package magma.api;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {
    @Override
    public <Return> Result<Return, Error> flatMap(Function<Value, Result<Return, Error>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public <Return> Result<Return, Error> map(Function<Value, Return> mapper) {
        return new Ok<>(mapper.apply(this.value));
    }

    @Override
    public Optional<Value> findValue() {
        return Optional.of(this.value);
    }

    @Override
    public void consume(Consumer<Value> whenOk, Consumer<Error> whenErr) {
        whenOk.accept(this.value);
    }
}
