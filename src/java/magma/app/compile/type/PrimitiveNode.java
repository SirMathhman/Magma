package magma.app.compile.type;

import magma.app.TypeCompiler;
import magma.app.compile.node.Node;

public enum PrimitiveNode implements Node {
    String("string"),
    Number("number"),
    Boolean("boolean"),
    Var("var"),
    Void("void"),
    Unknown("unknown");

    private final String value;

    PrimitiveNode(String value) {
        this.value = value;
    }

    public String generateNode() {
        return this.value;
    }

    public boolean isVar() {
        return PrimitiveNode.Var == this;
    }

    public String generateBeforeName() {
        return "";
    }

    public String generateSimple() {
        return TypeCompiler.generateNode(this);
    }

    public boolean is(String type) {
        return type.equals(this.name().toLowerCase());
    }
}
