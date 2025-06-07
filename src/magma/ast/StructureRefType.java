package magma.ast;

public record StructureRefType(String name) implements Type {
    @Override
    public String generate() {
        return name;
    }
}
