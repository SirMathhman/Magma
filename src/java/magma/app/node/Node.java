package magma.app.node;

import java.util.List;

public final class Node {
    private final NodeProperties<List<Node>> nodeLists;
    private final NodeProperties<String> strings;

    public Node() {
        this.strings = new NodeProperties<>(this::withStrings);
        this.nodeLists = new NodeProperties<>(this::withNodeLists);
    }

    public Node(NodeProperties<String> strings, NodeProperties<List<Node>> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private Node withNodeLists(NodeProperties<List<Node>> nodeLists) {
        return new Node(this.strings, nodeLists);
    }

    private Node withStrings(NodeProperties<String> properties) {
        return new Node(this.strings, this.nodeLists);
    }

    public NodeProperties<List<Node>> nodeLists() {
        return this.nodeLists;
    }

    public NodeProperties<String> strings() {
        return this.strings;
    }
}