package magma.app.string;

import java.util.function.Function;
import java.util.function.Supplier;

public record StringErr<Error>(Error error) implements StringResult<Error> {
    @Override
    public StringResult<Error> appendSlice(final String slice) {
        return this;
    }

    @Override
    public StringResult<Error> prepend(final String slice) {
        return this;
    }

    @Override
    public StringResult<Error> tryAppendResult(final Supplier<StringResult<Error>> other) {
        return this;
    }

    @Override
    public StringResult<Error> appendResult(final StringResult<Error> other) {
        return new StringErr<>(this.error);
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<Error, Return> whenError) {
        return whenError.apply(this.error);
    }
}
