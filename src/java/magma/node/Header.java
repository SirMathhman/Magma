package magma.node;

public sealed interface Header permits Constructor, Definition, Placeholder {
    String generate();
}
