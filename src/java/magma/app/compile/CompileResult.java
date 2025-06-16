package magma.app.compile;

import magma.api.Ok;
import magma.api.Result;

import java.util.function.Function;

public record CompileResult<Value>(Result<Value, CompileError> result) {
    public static <Value> CompileResult<Value> from(Value input) {
        return new CompileResult<>(new Ok<>(input));
    }

    public <Return> CompileResult<Return> flatMap(Function<Value, CompileResult<Return>> mapper) {
        return new CompileResult<>(this.result.flatMap(value -> mapper.apply(value).result));
    }

    public <Return> CompileResult<Return> mapValue(Function<Value, Return> mapper) {
        return new CompileResult<>(this.result.mapValue(mapper));
    }

    public <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenErr) {
        return this.result.match(whenOk, whenErr);
    }
}
