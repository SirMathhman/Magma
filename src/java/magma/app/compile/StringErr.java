package magma.app.compile;

import magma.app.compile.rule.or.Accumulator;

import java.util.function.Supplier;

public record StringErr<Error>(Error error) implements StringResult<Error> {
    @Override
    public StringResult<Error> appendResult(Supplier<StringResult<Error>> generate) {
        return new StringErr<>(this.error());
    }

    @Override
    public StringResult<Error> prependSlice(String slice) {
        return new StringErr<>(this.error());
    }

    @Override
    public StringResult<Error> appendSlice(String infix) {
        return this;
    }

    @Override
    public Accumulator<String, Error> attachToState(Accumulator<String, Error> state) {
        return state.withError(this.error());
    }

}
