package magma.app.compile.rule.modify;

import magma.app.compile.CompileError;
import magma.app.compile.error.context.StringContext;

import java.util.Optional;

public record PrefixModifier(String prefix) implements Modifier {
    @Override
    public String generate(String value) {
        return this.prefix + value;
    }

    @Override
    public Optional<String> modify(String input) {
        if (input.startsWith(this.prefix)) {
            final var slice = input.substring(this.prefix.length());
            return Optional.of(slice);
        }

        return Optional.empty();
    }

    @Override
    public CompileError createError(String input) {
        return new CompileError("Prefix '" + this.prefix + "' not present", new StringContext(input));
    }
}