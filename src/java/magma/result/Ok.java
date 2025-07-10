package magma.result;

import java.util.function.Function;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {
    @Override
    public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
        return whenOk.apply(this.value);
    }
}
