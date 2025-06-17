package magma.app.compile.string;

import magma.app.compile.FormattedError;
import magma.app.compile.rule.or.Accumulator;

import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    public StringOk() {
        this("");
    }

    @Override
    public StringResult appendResult(Supplier<StringResult> generate) {
        return generate.get()
                .prepend(this.value);
    }

    @Override
    public StringResult prepend(String slice) {
        return new StringOk(slice + this.value);
    }

    @Override
    public StringResult appendSlice(String infix) {
        return new StringOk(this.value + infix);
    }

    @Override
    public Accumulator<String, FormattedError> attachToState(Accumulator<String, FormattedError> state) {
        return state.withValue(this.value);
    }
}
