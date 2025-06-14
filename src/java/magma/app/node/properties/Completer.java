package magma.app.node.properties;

import magma.app.node.Node;

public interface Completer<T> {
    Node complete(Properties<Node, T> properties);
}
