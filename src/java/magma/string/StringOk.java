package magma.string;

import java.util.function.Function;
import java.util.function.Supplier;

public record StringOk<Error>(String value) implements StringResult<Error> {
    public StringOk() {
        this("");
    }

    @Override
    public StringResult<Error> appendSlice(final String slice) {
        return new StringOk<>(this.value + slice);
    }

    @Override
    public StringResult<Error> prepend(final String slice) {
        return new StringOk<>(slice + this.value);
    }

    @Override
    public StringResult<Error> tryAppendResult(final Supplier<StringResult<Error>> other) {
        return this.appendResult(other.get());
    }

    @Override
    public StringResult<Error> appendResult(final StringResult<Error> other) {
        return other.prepend(this.value);
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<Error, Return> whenError) {
        return whenOk.apply(this.value);
    }
}
