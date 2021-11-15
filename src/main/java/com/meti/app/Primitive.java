package com.meti.app;

import com.meti.app.node.Node;

public enum Primitive {
    U16(false, 16),
    I16(true, 16);

    private final boolean signed;
    private final int bits;

    Primitive(boolean signed, int bits) {
        this.signed = signed;
        this.bits = bits;
    }

    public Node asField(String name, Node value) {
        return new CodaPrimitiveField(signed, bits, name, value);
    }

    public Node asField(String name) {
        return new PrimitiveField(signed, bits, name);
    }

    public Node asFieldWithOnset(String name, Node onset) {
        return new OnsetPrimitiveField(signed, bits, name, onset);
    }
}
