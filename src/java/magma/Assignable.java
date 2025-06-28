package magma;

sealed interface Assignable extends MethodHeader permits Definition, Placeholder {
    String generate();
}
