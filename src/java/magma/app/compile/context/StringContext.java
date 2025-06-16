package magma.app.compile.context;

public record StringContext(String input) implements Context {
    @Override
    public String display() {
        return this.input;
    }
}
