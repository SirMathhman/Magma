package magma.app.compile;

import magma.app.compile.rule.or.Accumulator;

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
    public Accumulator<String, FormattedError> attachToState(Accumulator<String, FormattedError> state) {
        return state.withError(this.error());
    }
}
