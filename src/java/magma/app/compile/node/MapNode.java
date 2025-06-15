package magma.app.compile.node;

import magma.app.compile.node.properties.MapNodeProperties;
import magma.app.compile.node.properties.NodeProperties;

import java.util.List;

public final class MapNode implements Node {
    private final NodeProperties<List<Node>, Node> nodeLists;
    private final NodeProperties<String, Node> strings;

    public MapNode() {
        this.strings = new MapNodeProperties<>(strings1 -> withStrings(this, strings1));
        this.nodeLists = new MapNodeProperties<>(nodeLists1 -> withNodeLists(this, nodeLists1));
    }

    public MapNode(NodeProperties<String, Node> strings, NodeProperties<List<Node>, Node> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private static Node withNodeLists(MapNode mapNode, NodeProperties<List<Node>, Node> nodeLists) {
        return new MapNode(mapNode.strings, nodeLists);
    }

    private static Node withStrings(MapNode mapNode, NodeProperties<String, Node> strings) {
        return new MapNode(strings, mapNode.nodeLists);
    }

    @Override
    public NodeProperties<List<Node>, Node> nodeLists() {
        return this.nodeLists;
    }

    @Override
    public NodeProperties<String, Node> strings() {
        return this.strings;
    }
}