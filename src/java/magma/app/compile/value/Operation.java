package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.CompileState;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Operation(Value left, String targetInfix, Value right) implements Value {
    @Override
    public String generate() {
        return this.left.generate() + " " + this.targetInfix + " " + this.right.generate();
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }

    @Override
    public Option<String> generateAsEnumValue(String structureName) {
        return new None<String>();
    }
}
