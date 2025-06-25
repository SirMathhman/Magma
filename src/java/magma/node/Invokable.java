package magma.node;

public record Invokable(Caller caller, Value argument) implements Value {
    @Override
    public String generate() {
        return this.caller.generate() + "(" + this.argument.generate() + ")";
    }
}
