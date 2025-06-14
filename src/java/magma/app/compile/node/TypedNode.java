package magma.app.compile.node;

public interface TypedNode<Self> {
    boolean is(String type);

    Self retype(String type);
}
