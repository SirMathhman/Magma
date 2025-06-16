package magma.app.compile.error;

import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.rule.State;

import java.util.function.Function;
import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    @Override
    public StringResult appendResult(Supplier<StringResult> other) {
        return other.get()
                .prependSlice(this.value);
    }

    @Override
    public StringResult complete(Function<String, String> mapper) {
        return new StringOk(mapper.apply(this.value));
    }

    @Override
    public StringResult prependSlice(String slice) {
        return new StringOk(slice + this.value);
    }

    @Override
    public StringResult appendSlice(String slice) {
        return new StringOk(this.value + slice);
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Ok<>(this.value);
    }

    @Override
    public State<String> attachToState(State<String> state) {
        return state.withValue(this.value);
    }
}
