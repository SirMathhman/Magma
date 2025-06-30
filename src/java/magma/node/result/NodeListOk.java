package magma.node.result;

import magma.node.EverythingNode;
import magma.node.MapNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record NodeListOk(List<EverythingNode> list) implements NodeListResult {
    public NodeListOk() {
        this(new ArrayList<>());
    }

    @Override
    public NodeListResult add(final NodeResult<EverythingNode> other) {
        return other.match((Function<EverythingNode, NodeListResult>) everythingNode -> {
            this.list.add(everythingNode);
            return this;
        }, NodeListErr::new);
    }

    @Override
    public NodeResult<EverythingNode> toNode(final String key) {
        return new NodeOk<>(new MapNode().withNodeList(key, this.list));
    }
}
