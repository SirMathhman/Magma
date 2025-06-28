package magma;

public record StructureHeader(String type, ListLike<String> annotations, String beforeKeyword, String name,
                              Optional<String> maybeAfterImplements, ListLike<Parameter> parameters)
        implements StructureDefinition {
    @Override
    public String generate() {
        return this.type + " " + this.name;
    }
}