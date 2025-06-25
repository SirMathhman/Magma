package magma.node;

import java.util.Optional;

public record Placeholder(String input) implements CType,
        JavaHeader,
        Value,
        JavaType,
        CHeader,
        JavaClassSegment,
        JavaParameter,
        CParameter {
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

    @Override
    public Optional<String> findBaseName() {
        return Optional.empty();
    }

    @Override
    public boolean isNamed(final String name) {
        return false;
    }

    @Override
    public CParameter toCParameter() {
        return this;
    }
}
