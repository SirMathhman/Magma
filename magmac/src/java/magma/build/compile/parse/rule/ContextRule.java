package magma.build.compile.parse.rule;

import magma.api.result.Result;
import magma.build.compile.error.CompileParentError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ParsingResult;

public record ContextRule(String message, Rule child) implements Rule {
    private ParsingResult toNode0(String input) {
        return Rules.toNode(child, input).mapErr(err -> new CompileParentError(message, input, err));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}
