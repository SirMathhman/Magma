package magma.app.node;

import magma.app.node.properties.NodeProperties;

import java.util.List;

public interface Node {
    NodeProperties<List<Node>, Node> nodeLists();

    NodeProperties<String, Node> strings();
}
