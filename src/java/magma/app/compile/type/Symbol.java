package magma.app.compile.type;

import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.define.Definition;

public record Symbol(String value) implements Type {
    public String generateType() {
        return this.value;
    }

    public Type resolve(CompileState state) {
        return state.stack().resolveNode(this.value)
                .map((Definition definition) -> {
                    return definition.findType();
                })
                .orElse(PrimitiveType.Unknown);
    }

    public String generateBeforeName() {
        return "";
    }

    public String generateSimple() {
        return ValueCompiler.generateValue(this);
    }

    public boolean is(String type) {
        return false;
    }
}
