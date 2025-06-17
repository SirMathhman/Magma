package magma;

public record CompileError(String message) implements Error {
    @Override
    public String display() {
        return this.message;
    }
}
