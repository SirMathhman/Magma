package magma.ast;

public class StringType implements Type {
    @Override
    public String generate() {
        return "string";
    }
}
