package magma.node;

public record JavaDefinition(String beforeType, JavaType type, String name) implements JavaHeader, JavaClassSegment {
    public CDefinition toCDefinition() {
        return this.toCDefinition("");
    }

    public CDefinition toCDefinition(final String suffix) {
        return new CDefinition(this.beforeType, this.type.toCType(), this.name + suffix);
    }
}