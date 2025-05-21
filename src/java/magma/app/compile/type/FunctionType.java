package magma.app.compile.type;

import magma.api.collect.list.Iterable;
import magma.app.TypeCompiler;

public record FunctionType(Iterable<String> args, String returns) implements Type {
    @Override
    public boolean isFunctional() {
        return true;
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
