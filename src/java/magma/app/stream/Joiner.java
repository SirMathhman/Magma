package magma.app.stream;

import java.util.Optional;

public class Joiner implements CollectorLike<String, Optional<String>> {
    @Override
    public Optional<String> createInitial() {
        return Optional.empty();
    }

    @Override
    public Optional<String> fold(final Optional<String> current, final String input) {
        return Optional.of(current.map(inner -> inner + input)
                .orElse(""));
    }
}
