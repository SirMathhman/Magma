package magma.app.compile.node;

public interface TypedNode<Self> {
    Self retype(String type);

    boolean is(String type);
}
