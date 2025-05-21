package magma.app.compile.type;

import magma.app.TypeCompiler;

public enum PrimitiveType implements Type {
    String("string"),
    Number("number"),
    Boolean("boolean"),
    Var("var"),
    Void("void"),
    Unknown("unknown");

    public final String value;

    PrimitiveType(String value) {
        this.value = value;
    }

    @Override
    public boolean isFunctional() {
        return false;
    }

    @Override
    public boolean isVar() {
        return PrimitiveType.Var == this;
    }

    @Override
    public String generateBeforeName() {
        return "";
    }

    @Override
    public String generateSimple() {
        return TypeCompiler.generateType(this);
    }
}
