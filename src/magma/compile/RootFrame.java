package magma.compile;

import magma.util.*;
import magma.ast.*;
public record RootFrame(StructureTypeSet structureTypes) implements StructureContainerFrame {
    public RootFrame() {
        this(new StructureTypeSet());
    }

    @Override
    public StructureContainerFrame defineStructureType(StructureType structureType) {
        return new RootFrame(structureTypes.define(structureType));
    }

    @Override
    public Option<StructureType> resolveType(String name) {
        return structureTypes.resolveType(name);
    }

    @Override
    public Option<Definition> resolveValue(String name) {
        return new None<>();
    }

    @Override
    public Iterator<Definition> iterDefinitions() {
        return Iterators.empty();
    }

    @Override
    public Option<TypeParam> resolveTypeParam(String name) {
        return new None<>();
    }
}
