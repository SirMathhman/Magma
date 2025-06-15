package magma.app.node;

import magma.app.node.properties.NodeProperties;

public interface Completer<Value, Node> {
    Node complete(NodeProperties<Value, Node> properties);
}
