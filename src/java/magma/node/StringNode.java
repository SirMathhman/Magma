package magma.node;

public record StringNode(String value) implements Value {
    @Override
    public String generate() {
        return this.value;
    }
}
