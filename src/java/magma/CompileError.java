package magma;

public record CompileError(String message, Context context) implements Error {
    @Override
    public String display() {
        return this.message + ": " + this.context.display();
    }
}
