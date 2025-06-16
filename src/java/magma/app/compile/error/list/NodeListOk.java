package magma.app.compile.error.list;

import magma.app.compile.error.node.NodeOk;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record NodeListOk(List<NodeWithEverything> nodes) implements NodeListResult<NodeWithEverything> {
    public NodeListOk() {
        this(new ArrayList<>());
    }

    @Override
    public NodeResult<NodeWithEverything> toNode(String key) {
        return new NodeOk(new MapNode().nodeLists()
                .with(key, this.nodes));
    }

    @Override
    public NodeListResult<NodeWithEverything> add(Supplier<NodeResult<NodeWithEverything>> other) {
        return other.get()
                .attachToList(this.nodes)
                .match(NodeListOk::new, NodeListErr::new);
    }
}
