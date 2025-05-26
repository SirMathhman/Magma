package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record AccessValue(Value child, String property) implements Value {
    public String generate() {
        return ValueCompiler.generateValue(this.child) + "." + this.property;
    }

    public Option<Value> toValue() {
        return new Some<Value>(this);
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }

    @Override
    public Option<String> generateAsEnumValue(String structureName) {
        return new None<String>();
    }
}
