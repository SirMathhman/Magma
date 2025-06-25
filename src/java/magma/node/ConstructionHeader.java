package magma.node;

public record ConstructionHeader(CType type) implements Caller {
    @Override
    public String generate() {
        return "new_" + this.type.generateSymbol();
    }
}
