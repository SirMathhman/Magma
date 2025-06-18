package magma.app.compile.node;

public interface MergingNode<Node> {
    Node merge(Node other);
}
