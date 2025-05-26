package magma.app.compile.value;

public sealed interface Argument permits Value {
    default boolean is(String type) {
        return false;
    }
}
