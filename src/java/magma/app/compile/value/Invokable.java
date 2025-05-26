package magma.app.compile.value;

import magma.api.collect.list.Iterable;
import magma.api.collect.list.List;
import magma.app.compile.node.Node;

import java.util.Objects;

public final class Invokable implements Node {
    private final Node node;
    private final List<Node> args;

    public Invokable(Node node, List<Node> args) {
        this.node = node;
        this.args = args;
    }

    public Node node() {
        return node;
    }

    public Iterable<Node> args() {
        return args;
    }
}
