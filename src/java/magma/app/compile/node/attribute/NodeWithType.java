package magma.app.compile.node.attribute;

public interface NodeWithType<Self> {
    Self retype(String type);

    boolean is(String type);
}
