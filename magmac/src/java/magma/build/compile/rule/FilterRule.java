package magma.build.compile.rule;

import magma.api.result.Result;
import magma.build.compile.CompileError;
import magma.build.compile.Error_;
import magma.build.compile.rule.result.ErrorRuleResult;
import magma.build.compile.rule.result.RuleResult;

public abstract class FilterRule implements Rule {
    protected final Rule child;

    public FilterRule(Rule child) {
        this.child = child;
    }

    @Override
    public RuleResult toNode(String input) {
        if (filter(input)) return child.toNode(input);
        return new ErrorRuleResult(new CompileError("Invalid filter: " + computeMessage(), input));
    }

    protected abstract String computeMessage();

    protected abstract boolean filter(String input);

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }
}
