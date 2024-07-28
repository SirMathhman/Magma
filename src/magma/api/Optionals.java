package magma.api;

import java.util.Optional;
import java.util.function.Supplier;

public class Optionals {
    public static <L, R> Optional<Tuple<L, R>> and(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<L> left, Supplier<Optional<R>> right) {
        return left.flatMap(leftValue -> right.get().map(rightValue -> new Tuple<>(leftValue, rightValue)));
    }
}
