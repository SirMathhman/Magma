package magma.build.compile.parse.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.attribute.MapAttributes;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.compile.parse.result.UntypedParsingResult;

public record EmptyRule(String name) implements Rule {
    private ParsingResult toNode0(String input) {
        return input.isEmpty()
                ? new UntypedParsingResult(new MapAttributes())
                : new ErrorParsingResult(new CompileError("Input is not empty.", input));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        if (node.has(name)) {
            return new Err<>(new CompileError("Node has property '" + name + "'.", node.toString()));
        } else {
            return new Ok<>("");
        }
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}
