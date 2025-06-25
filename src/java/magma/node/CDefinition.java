package magma.node;

public record CDefinition(String beforeType, CType type, String name) implements CHeader {
    @Override
    public String generate() {
        return Placeholder.generate(this.beforeType() + " ") + this.type()
                .generate() + " " + this.name();
    }
}