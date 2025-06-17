package magma.app.compile.error;

import magma.app.compile.rule.or.OrState;

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

    @Override
    public OrState<String, FormattedError> attachToState(OrState<String, FormattedError> state) {
        return switch (this) {
            case StringOk(String value) -> state.withValue(value);
            case StringErr(var error) -> state.withError(error);
        };
    }
}
