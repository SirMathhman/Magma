package magma;

sealed interface Assignable extends MethodHeader, Parameter permits Definition, Placeholder {
    String generate();
}
