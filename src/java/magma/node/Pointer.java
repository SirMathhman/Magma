package magma.node;

public record Pointer(CType type) implements CType {
    @Override
    public String generate() {
        return this.type.generate() + "*";
    }

    @Override
    public String generateSymbol() {
        return this.type.generateSymbol() + "_ptr";
    }
}
