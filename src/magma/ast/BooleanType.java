package magma.ast;

public class BooleanType implements Type {
    @Override
    public String generate() {
        return "boolean";
    }
}
