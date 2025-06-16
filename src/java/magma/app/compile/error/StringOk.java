package magma.app.compile.error;

import magma.app.compile.rule.OrState;

import java.util.function.Function;
import java.util.function.Supplier;

public record StringOk<Error>(String value) implements StringResult<Error> {
    @Override
    public StringResult<Error> appendResult(Supplier<StringResult<Error>> other) {
        return other.get()
                .prependSlice(this.value);
    }

    @Override
    public StringResult<Error> complete(Function<String, String> mapper) {
        return new StringOk<>(mapper.apply(this.value));
    }

    @Override
    public StringResult<Error> prependSlice(String slice) {
        return new StringOk<>(slice + this.value);
    }

    @Override
    public StringResult<Error> appendSlice(String slice) {
        return new StringOk<>(this.value + slice);
    }

    @Override
    public OrState<String, Error> attachToState(OrState<String, Error> state) {
        return state.withValue(this.value);
    }

    @Override
    public <Return> Return match(Function<String, Return> whenOk, Function<Error, Return> whenErr) {
        return whenOk.apply(this.value);
    }
}
