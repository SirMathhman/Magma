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

    private static NodeWithEverything withNodeLists(MapNode mapNode, NodeProperties<List<NodeWithEverything>, NodeWithEverything> nodeLists) {
        return new MapNode(mapNode.strings, nodeLists);
    }

    private static NodeWithEverything withStrings(MapNode mapNode, NodeProperties<String, NodeWithEverything> strings) {
        return new MapNode(strings, mapNode.nodeLists);
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