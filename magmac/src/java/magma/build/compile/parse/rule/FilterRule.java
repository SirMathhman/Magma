package magma.build.compile.parse.rule;

import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorRuleResult;
import magma.build.compile.parse.result.RuleResult;

public class FilterRule implements Rule {
    protected final Rule child;
    private final Filter filter;

    public FilterRule(Rule child, Filter filter) {
        this.child = child;
        this.filter = filter;
    }

    @Override
    public RuleResult toNode(String input) {
        if (filter.filter(input)) return child.toNode(input);
        return new ErrorRuleResult(new CompileError("Invalid filter: " + filter.computeMessage(), input));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }

}
