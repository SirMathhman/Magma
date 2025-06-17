package magma.app.compile.error;

import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    @Override
    public StringResult appendResult(Supplier<StringResult> generate) {
        StringOk stringOk = this;
        return generate.get()
                .prepend(stringOk.value());
    }

    @Override
    public StringResult prepend(String slice) {
        StringOk stringOk = this;
        return new StringOk(slice + stringOk.value());
    }

    @Override
    public StringResult appendSlice(String infix) {
        StringOk stringOk = this;
        return new StringOk(stringOk.value() + infix);
    }
}
