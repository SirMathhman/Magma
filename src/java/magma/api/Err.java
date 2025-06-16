package magma.api;

import java.util.Optional;
import java.util.function.Function;

public record Err<Value, Error>(Error error) implements Result<Value, Error> {
    @Override
    public <Return> Result<Return, Error> flatMap(Function<Value, Result<Return, Error>> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <Return> Result<Return, Error> map(Function<Value, Return> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public Optional<Value> findValue() {
        return Optional.empty();
    }
}
