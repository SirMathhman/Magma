package magma.app.compile.error.string;

import magma.app.compile.error.FormattedError;
import magma.app.compile.rule.or.OrState;

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
    public OrState<String, FormattedError> attachToState(OrState<String, FormattedError> state) {
        return state.withValue(this.value);
    }
}
