package magma.result;

import java.io.IOException;
import java.util.function.Function;

public record Err<Value>(IOException error) implements Result<Value> {
    @Override
    public <Return> Return match(final Function<Value, Return> whenOk, final Function<IOException, Return> whenError) {
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
