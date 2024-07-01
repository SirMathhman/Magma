package magma.build.compile.parse.rule.text.extract;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.compile.parse.result.UntypedParsingResult;
import magma.build.compile.error.Error_;
import magma.build.compile.attribute.MapAttributes;
import magma.build.compile.attribute.NodeAttribute;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.rule.Rules;
import magma.build.java.JavaOptionals;

public record ExtractNodeRule(String propertyKey, Rule child) implements Rule {
    private ParsingResult toNode0(String input) {
        var node = Rules.toNode(child, input);
        if (JavaOptionals.toNative(node.findError()).isPresent()) return node;

        return JavaOptionals.toNative(node.tryCreate())
                .map(NodeAttribute::new)
                .map(attribute -> new MapAttributes().with(propertyKey, attribute))
                .<ParsingResult>map(UntypedParsingResult::new)
                .orElse(new ErrorParsingResult(new CompileError("No name present: ", input)));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        var asNode = JavaOptionals.toNative(node.findNode(propertyKey));
        if (asNode.isEmpty()) return createErr(node);

        return child.fromNode(asNode.get());
    }

    private Err<String, Error_> createErr(Node node) {
        var format = "Node did not have attribute '%s' as a node.";
        var message = format.formatted(propertyKey);
        return new Err<>(new CompileError(message, node.toString()));
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}
