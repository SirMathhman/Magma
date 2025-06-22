package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.result.CompileError;
import magma.app.compile.result.NodeOk;
import magma.app.compile.result.NodeResult;
import magma.app.compile.result.StringErr;
import magma.app.compile.result.StringOk;
import magma.app.compile.result.StringResult;

public record StringRule(String key) implements Rule<Node> {
    @Override
    public NodeResult lex(final String input) {
        final var node = MapNode.empty()
                .withString(this.key, input);

        return new NodeOk(node);
    }

    @Override
    public StringResult generate(final Node node) {
        return node.findStringOrElse(this.key,
                StringOk::new,
                () -> new StringErr(new CompileError("String '" + this.key + "' not present", node.asString())));
    }
}