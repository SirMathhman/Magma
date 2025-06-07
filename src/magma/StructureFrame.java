package magma;

record StructureFrame(
        DefinitionSet members,
        StructureTypeSet structureTypes,
        TypeParamSet typeParams
) implements StructureContainerFrame {
    public StructureFrame(TypeParamSet typeParams) {
        this(new DefinitionSet(), new StructureTypeSet(), typeParams);
    }

    @Override
    public Option<Definition> resolveValue(String name) {
        return members.resolveValue(name);
    }

    @Override
    public Iterator<Definition> iterDefinitions() {
        return members.iter();
    }

    public StructureFrame define(Definition definition) {
        return new StructureFrame(members.add(definition), structureTypes, typeParams);
    }

    @Override
    public Option<StructureType> resolveType(String name) {
        return structureTypes.resolveType(name);
    }

    @Override
    public StructureContainerFrame defineStructureType(StructureType structureType) {
        return new StructureFrame(members, structureTypes.define(structureType), typeParams);
    }

    @Override
    public Option<TypeParam> resolveTypeParam(String name) {
        return typeParams.resolve(name);
    }
}
