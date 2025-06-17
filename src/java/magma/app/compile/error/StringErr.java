package magma.app.compile.error;

import java.util.function.Supplier;

public record StringErr(FormattedError error) implements StringResult {
    @Override
    public StringResult appendResult(Supplier<StringResult> generate) {
        return new StringErr(this.error());
    }

    @Override
    public StringResult prepend(String slice) {
        return new StringErr(this.error());
    }

    @Override
    public StringResult appendSlice(String infix) {
        return this;
    }
}
