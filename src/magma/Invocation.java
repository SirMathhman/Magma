package magma;

public record Invocation(Caller caller, List<Value> arguments) implements Value {
    @Override
    public String generate() {
        return caller.generate() + "(" + Main.generateNodes(arguments) + ")";
    }
}
