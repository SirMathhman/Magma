package magma.ast;

import java.util.List;

public record StructLiteral(String name, List<String> vals, List<String> fields) {}
