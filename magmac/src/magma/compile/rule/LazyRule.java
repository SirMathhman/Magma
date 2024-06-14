package magma.compile.rule;

import magma.api.Err;
import magma.api.Result;
import magma.compile.CompileException;
import magma.compile.Error_;
import magma.compile.JavaError;
import magma.compile.rule.result.EmptyRuleResult;
import magma.compile.rule.result.RuleResult;

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
                .orElse(new EmptyRuleResult());
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.map(inner -> inner.fromNode(node))
                .orElse(new Err<>(new JavaError(new CompileException("No child set."))));
    }
}
