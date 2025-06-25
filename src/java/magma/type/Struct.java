package magma.type;

public record Struct(String name) implements CType {
    @Override
    public String generate() {
        return "struct " + name;
    }

    @Override
    public String generateSymbol() {
        return name;
    }
}
