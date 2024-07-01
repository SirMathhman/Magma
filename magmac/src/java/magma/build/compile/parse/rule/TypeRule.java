package magma.build.compile.parse.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.CompileParentError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.java.JavaOptionals;

public record TypeRule(String type, Rule child) implements Rule {

    public static final String FORMAT = "Node was not of type '%s', but rather '%s'.";

    private ParsingResult toNode0(String input) {
        var result = Rules.toNode(child, input);
        if (JavaOptionals.toNative(result.findError()).isEmpty()) return result.withType(type);

        var format = "Cannot attach type '%s' because of child failure.";
        var message = format.formatted(type);
        return new ErrorParsingResult(new CompileParentError(message, input, JavaOptionals.toNative(result.findError()).get()));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        if (!node.is(type)) {
            var message = String.format(FORMAT, type, node.findType());
            return new Err<>(new CompileError(message, node.toString()));
        }

        return child.fromNode(node).mapErr(err -> {
            var format = "Cannot generate '%s' from node.";
            var message = format.formatted(type);
            return new CompileParentError(message, node.toString(), err);
        });
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}
