package magma.app.compile.value;

public sealed interface Argument extends Node permits Value {
    @Override
    default boolean is(String type) {
        return false;
    }
}
