package magma.app.compile.value;

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
    public String generateSimple() {
        return TypeCompiler.generateType(this);
    }
}
