package magma;

/**
 * Accesses a field on a parent {@link Value}.
 */
record FieldAccess(Value parent, String property) implements Value {
    @Override
    public String generate() {
        return parent.generate() + "." + property;
    }
}
