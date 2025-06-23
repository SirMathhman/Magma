package magma.app.compile.context;

public record StringContext(String value) implements Context {
    @Override
    public String display() {
        return value;
    }
}
