package magma.app.compile.error;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class NodeListOk<Error> implements NodeListResult<NodeWithEverything, NodeResult<NodeWithEverything, Error, StringResult<Error>>> {
    public final List<NodeWithEverything> nodes;

    public NodeListOk(List<NodeWithEverything> nodes) {
        this.nodes = nodes;
    }

    public NodeListOk() {
        this(new ArrayList<>());
    }

    @Override
    public NodeResult<NodeWithEverything, Error, StringResult<Error>> toNode(String key) {
        return new NodeOk<>(new MapNode().nodeLists()
                .with(key, this.nodes));
    }

    @Override
    public NodeListResult<NodeWithEverything, NodeResult<NodeWithEverything, Error, StringResult<Error>>> add(Supplier<NodeResult<NodeWithEverything, Error, StringResult<Error>>> other) {
        return other.get()
                .attachToList(this.nodes)
                .match(NodeListOk::new, NodeListErr::new);
    }
}
