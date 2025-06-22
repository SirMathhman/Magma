package magma.app.node;

public interface TypedNode<Self> {
    Self retype(String type);

    boolean is(String type);
}
