package magma.node;

public record ConstructionHeader(JavaType type) implements Caller {
    @Override
    public String generate() {
        return "new_" + this.type.toCType()
                .generateSymbol();
    }
}
