package magma.app.compile.node;

public interface TypedNode<Node> {
    boolean is(String type);

    Node retype(String type);
}
