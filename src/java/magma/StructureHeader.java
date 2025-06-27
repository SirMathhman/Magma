package magma;

public record StructureHeader(String type, ListLike<String> annotations, String beforeKeyword, String name,
                              Optional<String> maybeAfterImplements) implements StructureDefinition {
    @Override
    public String generate() {
        return this.type + " " + this.name;
    }
}