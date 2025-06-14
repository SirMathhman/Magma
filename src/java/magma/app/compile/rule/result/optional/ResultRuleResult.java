package magma.app.compile.rule.result.optional;

import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.rule.result.RuleResult;

import java.util.Optional;
import java.util.function.Function;

public record ResultRuleResult<Value>(Result<Value, CompileError> inner) implements RuleResult<Value> {
    @Override
    public <Return> RuleResult<Return> flatMap(Function<Value, RuleResult<Return>> mapper) {
        return new ResultRuleResult<>(this.inner.flatMapValue(value -> mapper.apply(value).unwrap()));
    }

    @Override
    public <Return> RuleResult<Return> mapValue(Function<Value, Return> mapper) {
        return new ResultRuleResult<>(this.inner.mapValue(mapper));
    }

    @Override
    public Optional<Value> findAsOption() {
        return this.inner.findValue();
    }

    @Override
    public Result<Value, CompileError> unwrap() {
        return this.inner;
    }

    @Override
    public <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenErr) {
        return this.inner.match(whenOk, whenErr);
    }
}
