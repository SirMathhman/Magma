package magma.app.compile.node;

public interface NodeWithType {
    Node retype(String type);

    boolean is(String type);
}
