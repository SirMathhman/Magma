package magma.build.compile.parse.text;

import magma.api.result.Result;
import magma.build.compile.CompileError;
import magma.build.compile.Error_;
import magma.build.compile.parse.result.ErrorRuleResult;
import magma.build.compile.parse.result.RuleResult;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.Rule;

public record LeftRule(String slice, Rule child) implements Rule {

    @Override
    public RuleResult toNode(String input) {
        if (input.startsWith(slice)) {
            var content = input.substring(slice.length());
            return child.toNode(content);
        } else {
            return new ErrorRuleResult(new CompileError(String.format("Input does not start with '%s'.", slice), input));
        }
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node).mapValue(inner -> slice + inner);
    }
}