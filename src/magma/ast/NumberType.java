package magma.ast;

public class NumberType implements Type {
    @Override
    public String generate() {
        return "number";
    }
}
