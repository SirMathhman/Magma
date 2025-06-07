package magma.compile;

import magma.util.*;
import magma.ast.*;
public record MethodFrame(DefinitionSet parameters) implements Frame {
    @Override
    public Option<StructureType> resolveType(String name) {
        return new None<>();
    }

    @Override
    public Option<Definition> resolveValue(String name) {
        return parameters.resolveValue(name);
    }

    public Iterator<Definition> iterDefinitions() {
        return parameters.iter();
    }

    @Override
    public Option<TypeParam> resolveTypeParam(String name) {
        return new None<>();
    }
}
