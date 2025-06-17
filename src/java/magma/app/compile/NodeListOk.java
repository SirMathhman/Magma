package magma.app.compile;

import magma.api.result.Err;
import magma.api.result.Ok;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class NodeListOk<Node extends NodeWithNodeLists<Node> & MergeNode<Node> & TypeNode<Node>, Error> implements NodeListResult<Node, NodeResult<Node, Error>> {
    private final List<Node> node;

    public NodeListOk(List<Node> node) {
        this.node = node;
    }

    public NodeListOk() {
        this(new ArrayList<>());
    }

    @Override
    public NodeListResult<Node, NodeResult<Node, Error>> add(Supplier<NodeResult<Node, Error>> action) {
        return switch (action.get()
                .appendTo(this.node)) {
            case Err<List<Node>, Error>(var error) -> new NodeListErr<>(error);
            case Ok<List<Node>, Error>(var value) -> new NodeListOk<>(value);
        };
    }

    @Override
    public NodeResult<Node, Error> toNode(NodeFactory<Node> factory, String key) {
        return new NodeOk<>(factory.create()
                .withNodeList(key, this.node));
    }
}
