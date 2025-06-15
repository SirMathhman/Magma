package magma.app.compile.node;

import magma.app.compile.node.properties.NodeProperties;

public interface NodeWithStrings<Node> {
    NodeProperties<String, Node> strings();
}
