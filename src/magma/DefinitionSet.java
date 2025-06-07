package magma;

public record DefinitionSet(List<Definition> definitions) {
    public DefinitionSet() {
        this(Lists.empty());
    }

    public Option<Definition> resolveValue(String name) {
        return definitions.iter()
                .filter(definition -> definition.name.equals(name))
                .next();
    }

    public Iterator<Definition> iter() {
        return definitions.iter();
    }

    public DefinitionSet add(Definition definition) {
        return new DefinitionSet(definitions.add(definition));
    }
}
