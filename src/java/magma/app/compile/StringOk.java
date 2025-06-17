package magma.app.compile;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.rule.or.Accumulator;

import java.util.function.Supplier;

public final class StringOk<Error> implements StringResult<Error> {
    private final String value;

    public StringOk(String value) {
        this.value = value;
    }

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

    @Override
    public Result<String, Error> toResult() {
        return new Ok<>(this.value);
    }
}
