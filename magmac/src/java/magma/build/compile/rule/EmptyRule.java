package magma.build.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.CompileError;
import magma.build.compile.Error_;
import magma.build.compile.attribute.MapAttributes;
import magma.build.compile.rule.result.ErrorRuleResult;
import magma.build.compile.rule.result.RuleResult;
import magma.build.compile.rule.result.UntypedRuleResult;

public record EmptyRule(String name) implements Rule {
    @Override
    public RuleResult toNode(String input) {
        return input.isEmpty()
                ? new UntypedRuleResult(new MapAttributes())
                : new ErrorRuleResult(new CompileError("Input is not empty.", input));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        if (node.has(name)) {
            return new Err<>(new CompileError("Node has property '" + name + "'.", node.toString()));
        } else {
            return new Ok<>("");
        }
    }
}
