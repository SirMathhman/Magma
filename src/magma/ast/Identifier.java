package magma.ast;

/**
 * Represents an identifier within generated code.
 */
public record Identifier(String value) implements Value, Type {
    @Override
    public String generate() {
        return value;
    }

}
