package magma;

public class Placeholder implements MethodHeader {
    private final String value;

    public Placeholder(final String value) {
        this.value = value;
    }

    static String generate(final String input) {
        return "/*" + input.replace("/*", "stat").replace("*/", "end") + "*/";
    }

    @Override
    public String generateWithAfterName(final String afterName) {
        return Placeholder.generate(this.value) + afterName;
    }
}
