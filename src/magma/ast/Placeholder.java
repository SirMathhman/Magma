package magma.ast;

import magma.Generator;

/**
 * Value that will be substituted at generation time.
 */
public record Placeholder(String input) implements Parameter, Value, Type {
    @Override
    public String generate() {
        return Generator.generatePlaceholder(input);
    }
}
