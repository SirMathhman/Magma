package magma.app.compile.rule.modify;

import magma.app.compile.CompileError;
import magma.app.compile.error.context.StringContext;

import java.util.Optional;

public record SuffixModifier(String suffix) implements Modifier {
    @Override
    public String generate(String value) {
        return value + this.suffix;
    }

    @Override
    public Optional<String> modify(String input) {
        if (input.endsWith(this.suffix)) {
            return Optional.of(input.substring(0, input.length() - this.suffix.length()));
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    public CompileError createError(String input) {
        return new CompileError("Suffix '" + this.suffix + "' not present", new StringContext(input));
    }
}
