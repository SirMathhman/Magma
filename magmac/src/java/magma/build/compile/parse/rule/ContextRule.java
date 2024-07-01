package magma.build.compile.parse.rule;

import magma.api.result.Result;
import magma.build.compile.error.CompileParentError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ParsingResult;

public record ContextRule(String message, Rule child) implements Rule {
    @Override
    public ParsingResult toNode(String input) {
        return child.toNode(input).mapErr(err -> new CompileParentError(message, input, err));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }
}
