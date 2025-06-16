package magma.app.compile.rule.truncate;

import java.util.Optional;

public record PrefixTruncator(String prefix) implements Truncator {
    @Override
    public Optional<String> truncate(String input) {
        if (!input.startsWith(this.prefix))
            return Optional.empty();

        return Optional.of(input.substring(this.prefix.length()));
    }

    @Override
    public String complete(String result) {
        return this.prefix + result;
    }

    @Override
    public String createErrorMessage() {
        return "Prefix '" + this.prefix + "' not present";
    }
}