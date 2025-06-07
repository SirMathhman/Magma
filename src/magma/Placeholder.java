package magma;

public record Placeholder(String input) implements Parameter, Value, Type {
    @Override
    public String generate() {
        return Main.generatePlaceholder(input);
    }
}
