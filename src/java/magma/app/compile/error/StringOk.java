package magma.app.compile.error;

import magma.app.compile.rule.or.OrState;

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

    @Override
    public OrState<String, FormattedError> attachToState(OrState<String, FormattedError> state) {
        return state.withValue(this.value());
    }
}
