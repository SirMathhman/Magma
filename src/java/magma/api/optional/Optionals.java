package magma.api.optional;

import java.util.Optional;

public class Optionals {
    private Optionals() {
    }

    public static <Value> OptionalLike<Value> empty() {
        return new JavaOptional<>(Optional.empty());
    }

    public static <Value> OptionalLike<Value> of(final Value value) {
        return new JavaOptional<>(Optional.of(value));
    }
}
