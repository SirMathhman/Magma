package magma.rule;

import magma.option.Option;
import magma.option.Some;
import magma.string.StringResult;

import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    @Override
    public StringResult appendSlice(final String slice) {
        return new StringOk(this.value + slice);
    }

    @Override
    public Option<String> toOption() {
        return new Some<>(this.value);
    }

    @Override
    public StringResult prepend(final String slice) {
        return new StringOk(slice + this.value);
    }

    @Override
    public StringResult appendResult(final Supplier<StringResult> other) {
        return other.get()
                .prepend(this.value);
    }
}
