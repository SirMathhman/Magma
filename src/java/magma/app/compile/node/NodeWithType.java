package magma.app.compile.node;

public interface NodeWithType<Self> {
    Self retype(String type);

    boolean is(String type);
}
