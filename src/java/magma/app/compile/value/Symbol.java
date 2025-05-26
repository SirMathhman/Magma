package magma.app.compile.value;

import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.define.Definition;
import magma.app.compile.node.Node;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Symbol(String value) implements Node, Type {
    @Override
    public String generate() {
        return this.value;
    }

    public Type resolve(CompileState state) {
        return state.stack().resolveNode(this.value)
                .map((Definition definition) -> {
                    return definition.findType();
                })
                .orElse(PrimitiveType.Unknown);
    }

    public Option<Node> toNode() {
        return new Some<Node>(this);
    }

    @Override
    public boolean isFunctional() {
        return false;
    }

    @Override
    public boolean isVar() {
        return false;
    }

    @Override
    public String generateBeforeName() {
        return "";
    }



    @Override
    public String generateSimple() {
        return ValueCompiler.generateValue(this);
    }
}
