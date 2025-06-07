package magma;

record FieldAccess(Value parent, String property) implements Value {
    @Override
    public String generate() {
        return parent.generate() + "." + property;
    }
}
