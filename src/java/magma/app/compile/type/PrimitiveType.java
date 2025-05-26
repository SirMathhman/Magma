package magma.app.compile.type;

public enum PrimitiveType implements Type {
    String("string"),
    Number("number"),
    Boolean("boolean"),
    Var("var"),
    Void("void"),
    Unknown("unknown");

    private final String value;

    PrimitiveType(String value) {
        this.value = value;
    }

    @Override
    public String generate() {
        return this.value;
    }

    public boolean isVar() {
        return PrimitiveType.Var == this;
    }

    public String generateBeforeName() {
        return "";
    }

    public String generateSimple() {
        return this.generate();
    }

    public boolean is(String type) {
        return type.equals(this.name().toLowerCase());
    }
}
