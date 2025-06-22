package magma.error;

public record CompileError(String message) implements Error {
    @Override
    public String display() {
        return this.message;
    }
}
