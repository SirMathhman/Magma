package magma;

public record Placeholder(String value) implements Definable {
    static String wrap(final String input) {
        return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
    }

    @Override
    public String generate() {
        return Placeholder.wrap(this.value);
    }
}
