package magma.app.compile.type.resolve.template;

import magma.api.collect.list.Iterable;
import magma.app.TypeCompiler;
import magma.app.compile.type.Type;

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
