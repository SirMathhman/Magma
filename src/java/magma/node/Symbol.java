package magma.node;

public record Symbol(String value) implements Value, JavaType {
    @Override
    public String generate() {
        return this.value;
    }

    @Override
    public CType toCType() {
        return new Struct(this.value);
    }
}
