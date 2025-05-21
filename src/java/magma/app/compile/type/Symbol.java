package magma.app.compile.type;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.TypeCompiler;
import magma.app.compile.value.Value;

public record Symbol(String value) implements Value, Type {
    public String generate() {
        return this.value;
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
        return TypeCompiler.generateType(this);
    }
}
