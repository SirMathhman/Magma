package magma.app.compile;

import magma.api.collect.iter.Iterable;
import magma.api.collect.list.List;
import magma.api.collect.list.Lists;

import java.util.function.Supplier;

public final class NodeListOk<Node extends NodeWithNodeLists<Node> & MergeNode<Node> & TypeNode<Node>, Error> implements
        NodeListResult<Node, NodeResult<Node, Error, Iterable<Error>>> {
    private final List<Node> node;

    public NodeListOk(List<Node> node) {
        this.node = node;
    }

    public NodeListOk() {
        this(Lists.empty());
    }

    @Override
    public NodeListResult<Node, NodeResult<Node, Error, Iterable<Error>>> add(Supplier<NodeResult<Node, Error, Iterable<Error>>> action) {
        return switch (action.get()) {
            case NodeOk<Node, Error, Iterable<Error>>(Node value) -> {
                this.node.add(value);
                yield new NodeListOk<>(this.node);
            }
            case NodeErr(Error error) -> new NodeListErr<>(error);
        };
    }

    @Override
    public NodeResult<Node, Error, Iterable<Error>> toNode(NodeFactory<Node> factory, String key) {
        return new NodeOk<>(factory.create()
                .withNodeList(key, this.node));
    }
}
