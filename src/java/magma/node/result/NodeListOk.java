package magma.node.result;

import magma.node.NodeWithNodeLists;
import magma.node.factory.NodeFactory;

import java.util.ArrayList;
import java.util.List;

public final class NodeListOk<Node extends NodeWithNodeLists<Node>> implements NodeListResult<Node> {
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
    public NodeListResult<Node> add(final NodeResult<Node> other) {
        return other.match(node -> {
            this.list.add(node);
            return this;
        }, NodeListErr::new);
    }

    @Override
    public NodeResult<Node> toNode(final String key) {
        return new NodeOk<>(this.nodeFactory.createNode().withNodeList(key, this.list));
    }
}
