package magma.build.compile.rule;

import magma.api.result.Result;
import magma.build.compile.CompileParentError;
import magma.build.compile.Error_;
import magma.build.compile.rule.result.RuleResult;

public record ContextRule(String message, Rule child) implements Rule {
    @Override
    public RuleResult toNode(String input) {
        return child.toNode(input).mapErr(err -> new CompileParentError(message, input, err));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }
}
