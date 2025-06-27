package magma;

public class Placeholder implements Assignable, StructureDefinition {
    private final String value;

    public Placeholder(final String value) {
        this.value = value;
    }

    static String generate(final String input) {
        return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
    }

    @Override
    public String generateWithAfterName(final String afterName) {
        return Placeholder.generate(this.value) + afterName;
    }

    public String generate() {
        return this.value;
    }
}
