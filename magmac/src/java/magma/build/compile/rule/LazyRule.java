package magma.build.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.CompileError;
import magma.build.compile.Error_;
import magma.build.compile.rule.result.ErrorRuleResult;
import magma.build.compile.rule.result.RuleResult;

import java.util.Optional;

public class LazyRule implements Rule {
    private Optional<Rule> child;

    public LazyRule() {
        this.child = Optional.empty();
    }

    public void setRule(Rule child) {
        this.child = Optional.of(child);
    }

    @Override
    public RuleResult toNode(String input) {
        return child
                .map(inner -> inner.toNode(input))
                .orElse(new ErrorRuleResult(new CompileError("Child was not set.", input)));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.map(inner -> inner.fromNode(node))
                .orElse(new Err<>(new CompileError("No child set.", node.toString())));
    }
}
