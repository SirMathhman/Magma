package magma.app.compile.error.node;

import magma.app.compile.rule.action.CompileError;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public record NodeErr<Node>(CompileError compileError) implements NodeResult<Node> {
    @Override
    public Optional<Node> findValue() {
        return Optional.empty();
    }

    @Override
    public NodeResult<Node> mergeResult(Supplier<NodeResult<Node>> other, BiFunction<Node, Node, Node> merger) {
        return this;
    }

    @Override
    public NodeResult<Node> mergeNode(Node node, BiFunction<Node, Node, Node> merger) {
        return this;
    }
}
