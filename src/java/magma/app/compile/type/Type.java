package magma.app.compile.type;

import magma.app.compile.node.Node;

public sealed interface Type extends Node permits FunctionType, Placeholder, PrimitiveType, Symbol, TemplateType, VariadicType {
    String generate();

    String generateBeforeName();
}
