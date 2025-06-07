package magma;

public record StructureTypeSet(List<StructureType> structureTypes) {
    public StructureTypeSet() {
        this(Lists.empty());
    }

    public StructureTypeSet define(StructureType structureType) {
        return new StructureTypeSet(structureTypes.add(structureType));
    }

    public Option<StructureType> resolveType(String name) {
        return this.structureTypes.iter()
                .filter(type -> type.isNamed(name))
                .next();
    }
}
