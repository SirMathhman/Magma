package magma.api.optional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record JavaOptional<T>(Optional<T> optional) implements OptionalLike<T> {

    @Override
    public <Return> OptionalLike<Return> map(final Function<T, Return> mapper) {
        return new JavaOptional<>(this.optional.map(mapper));
    }

    @Override
    public void ifPresent(final Consumer<T> consumer) {
        this.optional.ifPresent(consumer);
    }

    @Override
    public T orElse(final T other) {
        return this.optional.orElse(other);
    }
}
