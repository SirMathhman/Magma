package magma.app.compile;

import magma.api.collect.iter.Iterable;

import java.util.function.Supplier;

public record StringOk<Error>(String value) implements StringResult<Error, Iterable<Error>> {
    public StringOk() {
        this("");
    }

    @Override
    public StringResult<Error, Iterable<Error>> appendResult(Supplier<StringResult<Error, Iterable<Error>>> generate) {
        return generate.get()
                .prependSlice(this.value);
    }

    @Override
    public StringResult<Error, Iterable<Error>> prependSlice(String slice) {
        return new StringOk<>(slice + this.value);
    }

    @Override
    public StringResult<Error, Iterable<Error>> appendSlice(String infix) {
        return new StringOk<>(this.value + infix);
    }

    @Override
    public Accumulator<String, Error, Iterable<Error>> attachToAccumulator(Accumulator<String, Error, Iterable<Error>> state) {
        return state.withValue(this.value);
    }

}
