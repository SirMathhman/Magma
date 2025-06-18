package magma.app.compile.rule.modify;

import java.util.Optional;

public class PrefixModifier implements Modifier {
    final String prefix;

    public PrefixModifier(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String complete(String value) {
        return this.prefix + value;
    }

    @Override
    public Optional<String> truncate(String input) {
        if (input.startsWith(this.prefix)) {
            final var slice = input.substring(this.prefix.length());
            return Optional.of(slice);
        }
        else
            return Optional.empty();
    }
}