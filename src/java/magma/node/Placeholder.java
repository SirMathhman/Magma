package magma.node;

public record Placeholder(String input) implements CType, JavaHeader, Value, JavaType, CHeader {
    public static String generate(final String input) {
        final var replaced = input.replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    @Override
    public String generate() {
        return Placeholder.generate(this.input);
    }

    @Override
    public String generateSymbol() {
        return Placeholder.generate(this.input);
    }

    @Override
    public CType toCType() {
        return this;
    }
}
