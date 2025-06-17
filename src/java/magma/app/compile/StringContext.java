package magma.app.compile;

public record StringContext(String value) implements Context {
    @Override
    public String display() {
        return this.value;
    }
}
