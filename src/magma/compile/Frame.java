package magma.compile;

import magma.util.*;
import magma.ast.*;
/**
 * A scope of definitions and type parameters used during compilation.
 */
public interface Frame {
    Option<StructureType> resolveType(String name);

    Option<Definition> resolveValue(String name);

    Iterator<Definition> iterDefinitions();

    Option<TypeParam> resolveTypeParam(String name);
}
