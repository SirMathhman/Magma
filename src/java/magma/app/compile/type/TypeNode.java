package magma.app.compile.type;

public interface TypeNode<Node> {
    boolean is(String type);

    Node retype(String type);
}
