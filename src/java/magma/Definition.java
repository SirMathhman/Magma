package magma;

public record Definition(String beforeType, String name, String type) implements MethodHeader {
    @Override
    public String generate(final String afterName) {
        return Placeholder.generatePlaceholder(this.beforeType) + " " + this.name + afterName + " : " + this.type;
    }
}