package magma.api;

import java.util.Optional;
import java.util.function.Function;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {
    @Override
    public <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr) {
        return whenOk.apply(this.value);
    }

    @Override
    public <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper) {
        return new Ok<>(mapper.apply(this.value));
    }

    @Override
    public Optional<Value> findValue() {
        return Optional.of(this.value);
    }
}
