package magma.app.compile.error;

import magma.api.Result;

import java.util.function.Function;

public interface CompileResult<Value> {
    <Return> CompileResult<Return> flatMap(Function<Value, CompileResult<Return>> mapper);

    <Return> CompileResult<Return> mapValue(Function<Value, Return> mapper);

    <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenErr);

    Result<Value, CompileError> result();
}