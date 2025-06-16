package magma.app.compile.rule.truncate;

import java.util.Optional;

public record SuffixTruncator(String suffix) implements Truncator {
    @Override
    public Optional<String> truncate(String input) {
        if (input.endsWith(this.suffix))
            return Optional.of(input.substring(0, input.length() - this.suffix.length()));
        return Optional.empty();
    }

    @Override
    public String generate(String result) {
        return result + this.suffix;
    }
}
