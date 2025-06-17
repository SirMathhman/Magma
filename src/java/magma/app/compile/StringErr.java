package magma.app.compile;

import magma.api.collect.iter.Iterable;

import java.util.function.Supplier;

public record StringErr<Error>(Error error) implements StringResult<Error, Iterable<Error>> {
    @Override
    public StringResult<Error, Iterable<Error>> appendResult(Supplier<StringResult<Error, Iterable<Error>>> generate) {
        return new StringErr<>(this.error());
    }

    @Override
    public StringResult<Error, Iterable<Error>> prependSlice(String slice) {
        return new StringErr<>(this.error());
    }

    @Override
    public StringResult<Error, Iterable<Error>> appendSlice(String infix) {
        return this;
    }

    @Override
    public Accumulator<String, Error, Iterable<Error>> attachToAccumulator(Accumulator<String, Error, Iterable<Error>> state) {
        return state.withError(this.error());
    }
}
