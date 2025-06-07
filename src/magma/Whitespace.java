package magma;

class Whitespace implements Parameter, Generating, ValueArgument, TypeArgument {
    @Override
    public String generate() {
        return "";
    }
}
