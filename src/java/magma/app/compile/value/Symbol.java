package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.app.TypeCompiler;
import magma.app.compile.type.Type;

public record Symbol(String value) implements Value, Type {
    public String generate() {
        return this.value;
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
