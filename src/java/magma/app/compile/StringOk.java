package magma.app.compile;

import java.util.function.Supplier;

public record StringOk<Error>(String value) implements StringResult<Error> {
    public StringOk() {
        this("");
    }

    @Override
    public StringResult<Error> appendResult(Supplier<StringResult<Error>> generate) {
        return generate.get()
                .prependSlice(this.value);
    }

    @Override
    public StringResult<Error> prependSlice(String slice) {
        return new StringOk<>(slice + this.value);
    }

    @Override
    public StringResult<Error> appendSlice(String infix) {
        return new StringOk<>(this.value + infix);
    }

    @Override
    public Accumulator<String, Error> attachToState(Accumulator<String, Error> state) {
        return state.withValue(this.value);
    }

}
