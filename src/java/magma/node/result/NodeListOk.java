package magma.node.result;

import magma.error.FormatError;
import magma.node.NodeWithNodeLists;
import magma.node.factory.NodeFactory;

import java.util.ArrayList;
import java.util.List;

public final class NodeListOk<Node extends NodeWithNodeLists<Node>> implements NodeListResult<Node, FormatError> {
    private final List<Node> list;
    private final NodeFactory<Node> nodeFactory;

    private NodeListOk(final List<Node> list, final NodeFactory<Node> nodeFactory) {
        this.list = list;
        this.nodeFactory = nodeFactory;
    }

    public NodeListOk(final NodeFactory<Node> nodeFactory) {
        this(new ArrayList<>(), nodeFactory);
    }

    @Override
    public NodeListResult<Node, FormatError> add(final NodeResult<Node, FormatError> other) {
        return other.match(node -> {
            this.list.add(node);
            return this;
        }, NodeListErr::new);
    }

    @Override
    public NodeResult<Node, FormatError> toNode(final String key) {
        return new NodeOk<>(this.nodeFactory.createNode().withNodeList(key, this.list));
    }
}
