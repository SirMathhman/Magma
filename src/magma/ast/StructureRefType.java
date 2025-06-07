package magma.ast;

import magma.util.*;
import magma.compile.*;
public record StructureRefType(String name) implements Type {
    @Override
    public String generate() {
        return name;
    }
}
