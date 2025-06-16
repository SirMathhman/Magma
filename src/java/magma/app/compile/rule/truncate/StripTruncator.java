package magma.app.compile.rule.truncate;

import java.util.Optional;

public class StripTruncator implements Truncator {
    @Override
    public Optional<String> truncate(String input) {
        return Optional.of(input.strip());
    }

    @Override
    public String generate(String result) {
        return result;
    }
}
