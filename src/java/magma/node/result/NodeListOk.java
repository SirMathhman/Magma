package magma.node.result;

import magma.node.EverythingNode;
import magma.node.factory.MapNodeFactory;
import magma.node.factory.NodeFactory;

import java.util.ArrayList;
import java.util.List;

public final class NodeListOk implements NodeListResult<EverythingNode> {
    private final List<EverythingNode> list;
    private final NodeFactory<EverythingNode> nodeFactory;

    public NodeListOk(final List<EverythingNode> list, final NodeFactory<EverythingNode> nodeFactory) {
        this.list = list;
        this.nodeFactory = nodeFactory;
    }

    public NodeListOk() {
        this(new ArrayList<>(), new MapNodeFactory());
    }

    @Override
    public NodeListResult<EverythingNode> add(final NodeResult<EverythingNode> other) {
        return other.match(everythingNode -> {
            this.list.add(everythingNode);
            return this;
        }, NodeListErr::new);
    }

    @Override
    public NodeResult<EverythingNode> toNode(final String key) {
        return new NodeOk<>(this.nodeFactory.createNode().withNodeList(key, this.list));
    }
}
