package magma.type;

import magma.Header;

public record Placeholder(String input) implements CType, Header {
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
}
