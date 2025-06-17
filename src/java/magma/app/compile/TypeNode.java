package magma.app.compile;

public interface TypeNode<Node> {
    boolean is(String type);

    Node retype(String type);
}
