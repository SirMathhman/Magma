package magma.app.node;

import magma.app.node.properties.MapNodeProperties;
import magma.app.node.properties.NodeProperties;

import java.util.List;

public final class MapNode implements Node {
    private final NodeProperties<List<Node>, Node> nodeLists;
    private final NodeProperties<String, Node> strings;

    public MapNode() {
        this.strings = new MapNodeProperties<>(this::withStrings);
        this.nodeLists = new MapNodeProperties<>(this::withNodeLists);
    }

    public MapNode(NodeProperties<String, Node> strings, NodeProperties<List<Node>, Node> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private Node withNodeLists(NodeProperties<List<Node>, Node> nodeLists) {
        return new MapNode(this.strings, nodeLists);
    }

    private Node withStrings(NodeProperties<String, Node> strings) {
        return new MapNode(strings, this.nodeLists);
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