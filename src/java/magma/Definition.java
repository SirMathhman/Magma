package magma;

public record Definition(String beforeType, String name, String type) implements MethodHeader {
    @Override
    public String generateWithAfterName(final String afterName) {
        return Placeholder.generate(this.beforeType) + " " + this.name + afterName + " : " + this.type;
    }
}