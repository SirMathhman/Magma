package magma.compile.rule.text;

import magma.api.Result;
import magma.compile.CompileException;
import magma.compile.attribute.Attributes;
import magma.compile.rule.Node;
import magma.compile.rule.Rule;
import magma.compile.rule.result.EmptyRuleResult;
import magma.compile.rule.result.RuleResult;

import java.util.Optional;

public record RightRule(Rule child, String slice) implements Rule {

    @Override
    public RuleResult toNode(String input) {
        if (!input.endsWith(slice)) {
            return new EmptyRuleResult();
        } else {
            var contentEnd = input.length() - slice.length();
            var content = input.substring(0, contentEnd);
            return child.toNode(content);
        }
    }

    @Override
    public Result<String, CompileException> fromNode(Node node) {
        return child.fromNode(node).mapValue(inner -> inner + slice);
    }
}