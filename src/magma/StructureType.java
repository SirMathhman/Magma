package magma;

public record StructureType(String name, Map<String, Definition> definitions) implements Type {
    @Override
    public String generate() {
        return "?";
    }

    public boolean isNamed(String name) {
        return this.name.equals(name);
    }

    public Option<Definition> find(String name) {
        return definitions.get(name);
    }
}
