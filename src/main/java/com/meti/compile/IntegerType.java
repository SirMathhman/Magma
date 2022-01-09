package com.meti.compile;

public class IntegerType implements Node {
    private final boolean signed;
    private final int bits;

    public IntegerType(boolean signed, int bits) {
        this.signed = signed;
        this.bits = bits;
    }

    @Override
    public Attribute apply(Attribute.Type type) throws AttributeException {
        return switch (type) {
            case Sign -> new BooleanAttribute(signed);
            case Bits -> new IntegerAttribute(bits);
            default -> throw new AttributeException(type);
        };
    }

    @Override
    public boolean is(Type type) {
        return type == Type.Integer;
    }
}