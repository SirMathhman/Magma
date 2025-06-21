package magma.api.optional;

import magma.api.collect.stream.JavaStream;
import magma.api.collect.stream.StreamLike;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    @Override
    public void ifPresent(final Consumer<T> consumer) {
        this.optional.ifPresent(consumer);
    }

    @Override
    public T orElseGet(final Supplier<T> supplier) {
        return this.optional.orElseGet(supplier);
    }

    @Override
    public T orElse(final T other) {
        return this.optional.orElse(other);
    }
}
