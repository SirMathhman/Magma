package magma.app.compile.type;

import magma.app.compile.node.Node;

public class PrimitiveType implements Node {
    public static final PrimitiveType Boolean = new PrimitiveType("boolean");
    public static final PrimitiveType Number = new PrimitiveType("number");
    public static final PrimitiveType String = new PrimitiveType("string");
    public static final PrimitiveType Unknown = new PrimitiveType("unknown");
    public static final PrimitiveType Var = new PrimitiveType("var");
    public static final PrimitiveType Void = new PrimitiveType("void");

    public final String value;

    PrimitiveType(String value) {
        this.value = value;
    }

    public boolean is(String type) {
        return type.equals(this.value);
    }
}
