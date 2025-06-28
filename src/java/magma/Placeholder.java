package magma;

public non-sealed class Placeholder implements Assignable, StructureDefinition {
    private final String value;

    public Placeholder(final String value) {
        this.value = value;
    }

    static String generate(final String input) {
        final var replaced = input.replace("/*", "start").replace("*/", "end");
        return "/*" + replaced + "*/";
    }

    @Override
    public String generateWithAfterName(final String afterName) {
        return Placeholder.generate(this.value) + afterName;
    }

    public String generate() {
        return this.value;
    }
}
