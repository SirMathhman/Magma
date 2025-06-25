package magma.type;

public record Placeholder(String input) implements CType {
    public static String generatePlaceholder(final String input) {
        final var replaced = input.replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    @Override
    public String generate() {
        return Placeholder.generatePlaceholder(input);
    }

    @Override
    public String generateSymbol() {
        return Placeholder.generatePlaceholder(input);
    }
}
