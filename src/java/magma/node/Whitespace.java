package magma.node;

public class Whitespace implements JavaParameter, CParameter {
    @Override
    public CParameter toCParameter() {
        return this;
    }

    @Override
    public String generate() {
        return "";
    }
}
