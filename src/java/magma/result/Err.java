package magma.result;

import magma.error.IOError;

import java.util.function.Function;

public record Err<Value>(IOError error) implements Result<Value> {
    @Override
    public <Return> Return match(final Function<Value, Return> whenOk, final Function<IOError, Return> whenError) {
        return whenError.apply(this.error);
    }

    @Override
    public <Return> Result<Return> map(final Function<Value, Return> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <Return> Result<Return> flatMap(final Function<Value, Result<Return>> mapper) {
        return new Err<>(this.error);
    }
}
