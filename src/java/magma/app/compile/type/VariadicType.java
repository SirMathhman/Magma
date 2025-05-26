package magma.app.compile.type;

import magma.app.TypeCompiler;

public record VariadicType(Type type) implements Type {
    public String generateType() {
        return TypeCompiler.generateType(this.type) + "[]";
    }

    public boolean isFunctional() {
        return false;
    }

    public boolean isVar() {
        return false;
    }

    public String generateBeforeName() {
        return "...";
    }

    public String generateSimple() {
        return TypeCompiler.generateType(this);
    }

    public boolean is(String type) {
        return "variadic".equals(type);
    }
}
