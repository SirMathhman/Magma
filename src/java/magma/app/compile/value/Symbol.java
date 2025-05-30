package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.CompileState;
import magma.app.compile.define.Definition;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Symbol(String value) implements Value, Type {
    @Override
    public String generate() {
        return this.value;
    }

    public Type resolve(CompileState state) {
        return state.stack().resolveValue(this.value)
                .map((Definition definition) -> {
                    return definition.findType();
                })
                .orElse(PrimitiveType.Unknown);
    }

    @Override
    public Option<Value> toValue() {
        return new Some<Value>(this);
    }

    @Override
    public Option<Value> findChild() {
        return new None<Value>();
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
    public Option<String> generateAsEnumValue(String structureName) {
        return new None<String>();
    }

    @Override
    public String generateSimple() {
        return this.generate();
    }
}
