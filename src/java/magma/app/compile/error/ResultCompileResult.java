package magma.app.compile.error;

import magma.api.Result;

import java.util.function.Function;

public record ResultCompileResult<Value>(Result<Value, CompileError> result) implements CompileResult<Value> {
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
