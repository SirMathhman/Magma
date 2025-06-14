package magma.app.node;

import magma.app.node.properties.Properties;

import java.util.List;

public interface Node {
    Properties<Node, String> strings();

    Properties<Node, List<Node>> nodeLists();

    Node merge(Node other);
}
