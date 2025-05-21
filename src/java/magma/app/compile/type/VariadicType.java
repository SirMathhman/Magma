package magma.app.compile.type;

import magma.app.TypeCompiler;

public record VariadicType(Type type) implements Type {

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
        return "...";
    }

    @Override
    public String generateSimple() {
        return TypeCompiler.generateType(this);
    }
}
