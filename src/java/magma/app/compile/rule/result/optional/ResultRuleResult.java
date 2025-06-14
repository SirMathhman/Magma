package magma.app.compile.rule.result.optional;

import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.rule.result.RuleResult;

import java.util.Optional;
import java.util.function.Function;

public record ResultRuleResult<Value>(Result<Value, CompileError> maybeValue) implements RuleResult<Value> {
    @Override
    public <Return> RuleResult<Return> flatMap(Function<Value, RuleResult<Return>> mapper) {
        return new ResultRuleResult<>(this.maybeValue.flatMapValue(value -> mapper.apply(value).unwrap()));
    }

    @Override
    public RuleResult<Value> mapValue(Function<Value, Value> mapper) {
        return new ResultRuleResult<>(this.maybeValue.mapValue(mapper));
    }

    @Override
    public Optional<Value> findAsOption() {
        return this.maybeValue.findValue();
    }

    @Override
    public Result<Value, CompileError> unwrap() {
        return this.maybeValue;
    }
}
