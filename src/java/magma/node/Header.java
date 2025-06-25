package magma.node;

public sealed interface Header permits Constructor, CDefinition, Placeholder {
    String generate();
}
