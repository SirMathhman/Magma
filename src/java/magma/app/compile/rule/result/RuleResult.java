package magma.app.compile.rule.result;

import magma.api.result.Result;
import magma.app.compile.CompileError;

import java.util.function.Function;

public interface RuleResult<Value> {
    <Return> RuleResult<Return> flatMap(Function<Value, RuleResult<Return>> mapper);

    <Return> RuleResult<Return> mapValue(Function<Value, Return> mapper);

    Result<Value, CompileError> unwrap();

    <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenErr);
}