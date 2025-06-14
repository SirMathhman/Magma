package magma.app.node.properties;

import magma.app.node.Node;

import java.util.List;

public final class PropertiesNode implements Node {
    private final Properties<Node, String> strings;
    private final Properties<Node, List<Node>> nodeLists;

    public PropertiesNode() {
        this.strings = new CompletingProperties<>(this::withStrings);
        this.nodeLists = new CompletingProperties<>(this::withNodeLists);
    }

    public PropertiesNode(Properties<Node, String> strings, Properties<Node, List<Node>> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private Node withNodeLists(Properties<Node, List<Node>> nodeLists) {
        return new PropertiesNode(this.strings, nodeLists);
    }

    private PropertiesNode withStrings(Properties<Node, String> properties) {
        return new PropertiesNode(properties, this.nodeLists);
    }

    @Override
    public Properties<Node, String> strings() {
        return this.strings;
    }

    @Override
    public Node merge(Node other) {
        return new PropertiesNode(this.strings.merge(other.strings()), this.nodeLists.merge(other.nodeLists()));
    }

    @Override
    public Properties<Node, List<Node>> nodeLists() {
        return this.nodeLists;
    }
}