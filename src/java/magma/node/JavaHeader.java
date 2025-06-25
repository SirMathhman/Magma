package magma.node;

public sealed interface JavaHeader permits Constructor, JavaDefinition, Placeholder {
    boolean isNamed(String name);
}
