package magma.build.compile.parse.rule.text;

import magma.api.result.Result;
import magma.build.compile.error.CompileParentError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.result.RuleResult;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.rule.Rule;

public record StripRule(Rule child, String left, String right) implements Rule {
    public static final String DEFAULT_LEFT = "left";
    public static final String DEFAULT_RIGHT = "right";

    public StripRule(Rule child) {
        this(child, DEFAULT_LEFT, DEFAULT_RIGHT);
    }

    @Override
    public RuleResult toNode(String input) {
        return child.toNode(input.strip()).mapErr(error -> new CompileParentError("Cannot strip input.", input, error));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        var leftIndent = node.findString(left).orElse("");
        var rightIndent = node.findString(right).orElse("");

        return child.fromNode(node)
                .mapValue(inner -> leftIndent + inner + rightIndent)
                .mapErr(err -> new CompileParentError("Cannot apply indentation: ", node.toString(), err));
    }
}
