package magma.rule;

import magma.error.CompileError;
import magma.node.TypedNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.Optional;

public record TypeRule<Node extends TypedNode<Node>>(String type, Rule<Node, NodeResult<Node>, StringResult> rule) implements Rule<Node, NodeResult<Node>, StringResult> {
    private Optional<Node> lex0(final String input) {
        return this.rule.lex(input).toOptional().map(node -> node.retype(this.type));
    }

    private Optional<String> generate0(final Node node) {
        if (node.is(this.type)) return this.rule.generate(node).toOptional();
        return Optional.empty();
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        return this.lex0(input).<NodeResult<Node>>map(NodeOk::new).orElseGet(
                () -> new NodeErr<Node>(new CompileError()));
    }

    @Override
    public StringResult generate(final Node node) {
        return this.generate0(node).<StringResult>map(StringOk::new).orElseGet(() -> new StringErr(new CompileError()));
    }
}
