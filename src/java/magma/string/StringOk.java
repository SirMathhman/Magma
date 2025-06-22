package magma.string;

import magma.error.FormattedError;

import java.util.function.Function;
import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    public StringOk() {
        this("");
    }

    @Override
    public StringResult appendSlice(final String slice) {
        return new StringOk(this.value + slice);
    }

    @Override
    public StringResult prepend(final String slice) {
        return new StringOk(slice + this.value);
    }

    @Override
    public StringResult tryAppendResult(final Supplier<StringResult> other) {
        return this.appendResult(other.get());
    }

    @Override
    public StringResult appendResult(final StringResult other) {
        return other.prepend(this.value);
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<FormattedError, Return> whenError) {
        return whenOk.apply(this.value);
    }
}
