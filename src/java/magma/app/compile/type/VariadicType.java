package magma.app.compile.type;

import magma.app.TypeCompiler;
import magma.app.compile.node.Node;

public record VariadicType(Node type) implements Node {
    public String generateNode() {
        return TypeCompiler.generateNode(this.type) + "[]";
    }

    public boolean isFunctional() {
        return false;
    }

    public boolean isVar() {
        return false;
    }

    public String generateBeforeName() {
        return "...";
    }

    public String generateSimple() {
        return TypeCompiler.generateNode(this);
    }

    public boolean is(String type) {
        return "variadic".equals(type);
    }
}
