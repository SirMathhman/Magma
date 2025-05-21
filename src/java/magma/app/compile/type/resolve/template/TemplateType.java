package magma.app.compile.type.resolve.template;

import magma.api.collect.list.Iterable;
import magma.app.compile.type.Type;

public record TemplateType(String base, Iterable<String> args) implements Type {

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
        return this.base;
    }
}
