package magma.app.compile.type.collect.array;

import magma.app.compile.type.Type;

public record ArrayType(Type childType) implements Type {
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
        return "";
    }
}
