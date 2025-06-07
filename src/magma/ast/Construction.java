package magma.ast;

/**
 * Invocation that creates a new instance of a {@link Type}.
 */
public record Construction(Type type) implements Caller {
    @Override
    public String generate() {
        return "new " + type.generate();
    }
}
