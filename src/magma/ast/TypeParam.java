package magma.ast;

public record TypeParam(String name) implements Type {
    @Override
    public String generate() {
        return name;
    }
}
