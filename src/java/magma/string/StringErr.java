package magma.string;

import magma.option.None;
import magma.option.Option;

import java.util.function.Supplier;

public record StringErr() implements StringResult {
    @Override
    public StringResult appendSlice(final String slice) {
        return new StringErr();
    }

    @Override
    public Option<String> toOption() {
        return new None<>();
    }

    @Override
    public StringResult prepend(final String slice) {
        return this;
    }

    @Override
    public StringResult appendResult(final Supplier<StringResult> other) {
        return this;
    }
}
