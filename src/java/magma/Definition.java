package magma;

public record Definition(String beforeType, String name, String type) implements Assignable {
    @Override
    public String generateWithAfterName(final String afterName) {
        return this.name + afterName + " : " + this.type;
    }

    @Override
    public String generate() {
        return this.generateWithAfterName("");
    }
}