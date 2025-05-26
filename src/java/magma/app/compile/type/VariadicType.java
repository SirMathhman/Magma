package magma.app.compile.type;

public record VariadicType(Type type) implements Type {
    @Override
    public String generate() {
        return this.type.generate() + "[]";
    }

    public boolean isFunctional() {
        return false;
    }

    public boolean isVar() {
        return false;
    }

    @Override
    public String generateBeforeName() {
        return "...";
    }

    @Override
    public String generateSimple() {
        return this.generate();
    }

    public boolean is(String type) {
        return "variadic".equals(type);
    }
}
