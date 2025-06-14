package magma.app.compile.rule.or;

import magma.app.compile.CompileError;
import magma.app.compile.error.Context;
import magma.app.compile.rule.result.ResultRuleResults;
import magma.app.compile.rule.result.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record OrState<Value>(Optional<Value> maybeValue, List<CompileError> errors) {
    public OrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    public OrState<Value> withValue(Value value) {
        if (this.maybeValue.isPresent())
            return this;

        return new OrState<>(Optional.of(value), this.errors);
    }

    public OrState<Value> withError(CompileError error) {
        this.errors.add(error);
        return this;
    }

    public RuleResult<Value> toResult(Context context) {
        return this.maybeValue.map(ResultRuleResults::createFromValue).orElseGet(() -> ResultRuleResults.createFromErrorWithContext("No valid combination present", context));
    }
}
