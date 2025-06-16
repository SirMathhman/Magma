package magma.app.compile.node;

import magma.app.compile.node.properties.MapNodeProperties;
import magma.app.compile.node.properties.NodeProperties;

import java.util.List;

public final class MapNode implements NodeWithEverything {
    private final NodeProperties<List<NodeWithEverything>, NodeWithEverything> nodeLists;
    private final NodeProperties<String, NodeWithEverything> strings;

    public MapNode() {
        this.strings = new MapNodeProperties<>(strings1 -> withStrings(this, strings1));
        this.nodeLists = new MapNodeProperties<>(nodeLists1 -> withNodeLists(this, nodeLists1));
    }

    public MapNode(NodeProperties<String, NodeWithEverything> strings, NodeProperties<List<NodeWithEverything>, NodeWithEverything> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private static NodeWithEverything withNodeLists(NodeWithStrings<NodeWithEverything> node, NodeProperties<List<NodeWithEverything>, NodeWithEverything> nodeLists) {
        return new MapNode(node.strings(), nodeLists);
    }

    private static NodeWithEverything withStrings(NodeWithNodeLists<NodeWithEverything> node, NodeProperties<String, NodeWithEverything> strings) {
        return new MapNode(strings, node.nodeLists());
    }

    @Override
    public NodeProperties<List<NodeWithEverything>, NodeWithEverything> nodeLists() {
        return this.nodeLists;
    }

    @Override
    public NodeProperties<String, NodeWithEverything> strings() {
        return this.strings;
    }

    @Override
    public String display() {
        return this.toString();
    }
}