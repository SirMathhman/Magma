package magma.node;

public record Symbol(String value) implements Value {
    @Override
    public String generate() {
        return this.value;
    }
}
