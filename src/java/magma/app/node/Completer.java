package magma.app.node;

public interface Completer<Value> {
    Node complete(NodeProperties<Value> properties);
}
