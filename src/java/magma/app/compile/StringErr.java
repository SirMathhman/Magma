package magma.app.compile;

import java.util.function.Supplier;

public record StringErr<Error, Iterable>(Error error) implements StringResult<Error, Iterable> {
    @Override
    public StringResult<Error, Iterable> appendResult(Supplier<StringResult<Error, Iterable>> generate) {
        return new StringErr<>(this.error);
    }

    @Override
    public StringResult<Error, Iterable> prependSlice(String slice) {
        return new StringErr<>(this.error);
    }

    @Override
    public StringResult<Error, Iterable> appendSlice(String infix) {
        return this;
    }

    @Override
    public Accumulator<String, Error, Iterable> attachToAccumulator(Accumulator<String, Error, Iterable> state) {
        return state.withError(this.error);
    }
}
