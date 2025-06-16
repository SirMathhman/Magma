package magma.app.compile.error;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.context.StringContext;

import java.util.function.Function;

public record ResultCompileResult<Value>(Result<Value, CompileError> result) implements CompileResult<Value> {
    public static <Value> CompileResult<Value> fromValue(Value input) {
        return new ResultCompileResult<>(new Ok<>(input));
    }

    public static CompileResult<String> fromStringError(String message, String input) {
        return new ResultCompileResult<>(new Err<>(new CompileError(message, new StringContext(input))));
    }

    @Override
    public <Return> CompileResult<Return> flatMap(Function<Value, CompileResult<Return>> mapper) {
        return new ResultCompileResult<>(this.result.flatMap(value -> mapper.apply(value)
                .result()));
    }

    @Override
    public <Return> CompileResult<Return> mapValue(Function<Value, Return> mapper) {
        return new ResultCompileResult<>(this.result.mapValue(mapper));
    }

    @Override
    public <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenErr) {
        return this.result.match(whenOk, whenErr);
    }
}
