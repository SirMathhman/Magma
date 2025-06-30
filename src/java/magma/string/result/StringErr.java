package magma.string.result;

import java.util.Optional;
import java.util.function.Function;

public record StringErr<Error>(Error error) implements StringResult<Error> {

    @Override
    public Optional<String> toOptional() {
        return Optional.empty();
    }

    @Override
    public StringResult<Error> appendResult(final StringResult<Error> other) {
        return this;
    }

    @Override
    public StringResult<Error> prependSlice(final String other) {
        return this;
    }

    @Override
    public StringResult<Error> appendSlice(final String slice) {
        return this;
    }

    @Override
    public StringResult<Error> flatMap(final Function<String, StringResult<Error>> mapper) {
        return this;
    }

    @Override
    public StringResult<Error> map(final Function<String, String> mapper) {
        return this;
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<Error, Return> whenErr) {
        return whenErr.apply(this.error);
    }
}
