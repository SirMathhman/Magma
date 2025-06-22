package magma.error;

public record CompileError(String message, String context) implements Error {
    @Override
    public String display() {
        return this.message + ": " + this.context;
    }
}
