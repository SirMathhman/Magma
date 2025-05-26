package magma.app.compile.node;

public interface Node {
    default boolean is(String type) {
        return false;
    }
}
