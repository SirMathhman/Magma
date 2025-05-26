package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.CompileState;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record StringValue(String value) implements Value {
    public String generate() {
        return "\"" + this.value + "\"";
    }

    public Option<Value> toValue() {
        return new Some<Value>(this);
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }


}
