package com.meti.app.compile.clang;

import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.attribute.Attribute;
import com.meti.app.compile.render.RenderException;
import com.meti.app.compile.stage.CompileException;
import com.meti.app.compile.text.Output;

public class IntegerTypeRenderer extends OutputRenderer {
    public IntegerTypeRenderer(Node identity) {
        super(identity, Node.Type.Integer);
    }

    @Override
    protected Output processValid() throws CompileException {
        var name = identity.apply(Attribute.Type.Name).asInput();
        var type = identity.apply(Attribute.Type.Type).asNode();
        var value = identity.apply(Attribute.Type.Value)
                .asNode()
                .apply(Attribute.Type.Value)
                .asOutput();

        var isSigned = type.apply(Attribute.Type.Sign).asBoolean();
        var bits = type.apply(Attribute.Type.Bits).asInteger();
        var prefix = switch (bits) {
            case 8 -> "char";
            case 16 -> "int";
            default -> throw new RenderException("Unknown bit quantity: " + bits);
        };

        var signedFlag = isSigned ? "" : "unsigned ";
        return name.toTrimmedOutput()
                .prepend(" ")
                .prepend(prefix)
                .prepend(signedFlag)
                .appendSlice("=")
                .appendOutput(value);
    }
}
