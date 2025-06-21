package magma.api.collect.stream;

import java.util.Optional;

public class Joiner implements Collector<String, Optional<String>> {
    @Override
    public Optional<String> createInitial() {
        return Optional.empty();
    }

    @Override
    public Optional<String> fold(final Optional<String> optional, final String input) {
        return Optional.of(optional.map(current -> current + input)
                .orElse(input));
    }
}
