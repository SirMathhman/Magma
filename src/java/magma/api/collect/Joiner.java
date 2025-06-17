package magma.api.collect;

import magma.api.collect.iter.Collector;

import java.util.Optional;

public record Joiner(String delimiter) implements Collector<String, Optional<String>> {
    @Override
    public Optional<String> createInitial() {
        return Optional.empty();
    }

    @Override
    public Optional<String> fold(Optional<String> maybe, String element) {
        return Optional.of(maybe.map(current -> current + this.delimiter + element)
                .orElse(element));
    }
}
