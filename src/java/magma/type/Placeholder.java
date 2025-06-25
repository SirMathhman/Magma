package magma.type;

public record Placeholder(String input) implements CType {
    public static String generate(final String input) {
        final var replaced = input.replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    @Override
    public String generate() {
        return Placeholder.generate(input);
    }

    @Override
    public String generateSymbol() {
        return Placeholder.generate(input);
    }
}
