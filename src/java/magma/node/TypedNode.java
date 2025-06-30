package magma.node;

public interface TypedNode<Node> {
    Node retype(String type);

    boolean is(String type);
}
