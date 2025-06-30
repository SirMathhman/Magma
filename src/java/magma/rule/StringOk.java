package magma.rule;

import magma.error.FormatError;
import magma.string.result.StringResult;

import java.util.Optional;
import java.util.function.Function;

public record StringOk(String value) implements StringResult<FormatError> {
    public StringOk() {
        this("");
    }

    @Override
    public Optional<String> toOptional() {
        return Optional.of(this.value);
    }

    @Override
    public StringResult<FormatError> appendResult(final StringResult<FormatError> other) {
        return other.prependSlice(this.value);
    }

    @Override
    public StringResult<FormatError> prependSlice(final String other) {
        return new StringOk(other + this.value);
    }

    @Override
    public StringResult<FormatError> appendSlice(final String slice) {
        return new StringOk(this.value + slice);
    }

    @Override
    public StringResult<FormatError> flatMap(final Function<String, StringResult<FormatError>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public StringResult<FormatError> map(final Function<String, String> mapper) {
        return new StringOk(mapper.apply(this.value));
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<FormatError, Return> whenErr) {
        return whenOk.apply(this.value);
    }
}
