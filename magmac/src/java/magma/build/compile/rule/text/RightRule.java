package magma.build.compile.rule.text;

import magma.api.result.Result;
import magma.build.compile.CompileError;
import magma.build.compile.Error_;
import magma.build.compile.rule.result.ErrorRuleResult;
import magma.build.compile.rule.result.RuleResult;
import magma.build.compile.rule.Node;
import magma.build.compile.rule.Rule;

public record RightRule(Rule child, String slice) implements Rule {

    @Override
    public RuleResult toNode(String input) {
        if (input.endsWith(slice)) {
            var contentEnd = input.length() - slice.length();
            var content = input.substring(0, contentEnd);
            return child.toNode(content);
        } else {
            return new ErrorRuleResult(new CompileError("Input does not end with '%s'.".formatted(slice), input));
        }
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node).mapValue(inner -> inner + slice);
    }
}