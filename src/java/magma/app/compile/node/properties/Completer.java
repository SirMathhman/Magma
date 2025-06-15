package magma.app.compile.node.properties;

public interface Completer<Value, Node> {
    Node complete(NodeProperties<Value, Node> properties);
}
