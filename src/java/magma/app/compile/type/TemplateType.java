package magma.app.compile.type;

import magma.api.collect.list.Iterable;

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
