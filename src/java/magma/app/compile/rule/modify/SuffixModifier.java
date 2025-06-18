package magma.app.compile.rule.modify;

import java.util.Optional;

public record SuffixModifier(String suffix) implements Modifier {
    @Override
    public String complete(String value) {
        return value + this.suffix;
    }

    @Override
    public Optional<String> truncate(String input) {
        if (input.endsWith(this.suffix))
            return Optional.of(input.substring(0, input.length() - this.suffix.length()));
        else
            return Optional.empty();
    }
}
