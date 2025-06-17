package magma.app.compile.node;

public interface TypeNode<Node> {
    boolean is(String type);

    Node retype(String type);
}
