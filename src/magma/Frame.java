package magma;

interface Frame {
    Option<StructureType> resolveType(String name);

    Option<Definition> resolveValue(String name);

    Iterator<Definition> iterDefinitions();

    Option<TypeParam> resolveTypeParam(String name);
}
