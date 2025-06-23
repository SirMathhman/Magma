package magma.app.compile.node.property;

public interface TypedNode<Self> {
    Self retype(String type);

    boolean is(String type);
}
