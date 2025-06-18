package magma.app.compile.node.attribute;

public interface MergingNode<Node> {
    Node merge(Node other);
}
