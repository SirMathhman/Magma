package jvm.optional;

import jvm.stream.JavaStream;
import magma.app.StreamLike;
import magma.app.optional.OptionalLike;

import java.util.Optional;
import java.util.function.Function;

public record JavaOptional<T>(Optional<T> optional) implements OptionalLike<T> {
    @Override
    public <Return> OptionalLike<Return> flatMap(final Function<T, OptionalLike<Return>> mapper) {
        return this.optional.map(mapper)
                .orElseGet(() -> new JavaOptional<>(Optional.empty()));
    }

    @Override
    public <Return> OptionalLike<Return> map(final Function<T, Return> mapper) {
        return new JavaOptional<>(this.optional.map(mapper));
    }

    @Override
    public StreamLike<T> stream() {
        return new JavaStream<>(this.optional.stream());
    }
}
