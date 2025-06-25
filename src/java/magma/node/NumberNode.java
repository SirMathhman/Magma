package magma.node;

public record NumberNode(String value) implements Value {
    @Override
    public String generate() {
        return this.value;
    }
}
