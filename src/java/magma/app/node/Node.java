package magma.app.node;

import magma.app.node.properties.MapNodeProperties;
import magma.app.node.properties.NodeProperties;

import java.util.List;

public final class Node {
    private final NodeProperties<List<Node>, Node> nodeLists;
    private final NodeProperties<String, Node> strings;

    public Node() {
        this.strings = new MapNodeProperties<>(this::withStrings);
        this.nodeLists = new MapNodeProperties<>(this::withNodeLists);
    }

    public Node(NodeProperties<String, Node> strings, NodeProperties<List<Node>, Node> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private Node withNodeLists(NodeProperties<List<Node>, Node> nodeLists) {
        return new Node(this.strings, nodeLists);
    }

    private Node withStrings(NodeProperties<String, Node> strings) {
        return new Node(strings, this.nodeLists);
    }

    public NodeProperties<List<Node>, Node> nodeLists() {
        return this.nodeLists;
    }

    public NodeProperties<String, Node> strings() {
        return this.strings;
    }
}