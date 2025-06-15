package magma.api.result;

import magma.api.Tuple;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record Err<Value, Error>(Error error) implements Result<Value, Error> {
    @Override
    public <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <Other> Result<Tuple<Value, Other>, Error> and(Supplier<Result<Other, Error>> other) {
        return new Err<>(this.error);
    }

    @Override
    public <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr) {
        return whenErr.apply(this.error);
    }

    @Override
    public Optional<Value> findValue() {
        return Optional.empty();
    }

    @Override
    public <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper) {
        return new Err<>(mapper.apply(this.error));
    }
}
