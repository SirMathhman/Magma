package magma;

public class Placeholder implements MethodHeader {
    private final String value;

    public Placeholder(final String value) {
        this.value = value;
    }

    static String generatePlaceholder(final String input) {
        return "/*" + input.replace("/*", "stat").replace("*/", "end") + "*/";
    }

    @Override
    public String generate(final String afterName) {
        return Placeholder.generatePlaceholder(this.value) + afterName;
    }
}
