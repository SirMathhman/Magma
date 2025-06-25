package magma.type;

public enum CPrimitive implements CType {
    Char("char"), Int("int");

    private final String value;

    CPrimitive(final String value) {
        this.value = value;
    }

    @Override
    public String generate() {
        return value;
    }

    @Override
    public String generateSymbol() {
        return value;
    }
}
