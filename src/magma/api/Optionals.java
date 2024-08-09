package magma.api;

import java.util.Optional;

public class Optionals {
    public static <L, R> Optional<Tuple<L, R>> and(Optional<L> left, Optional<R> right) {
        return left.flatMap(leftValue -> right.map(rightValue -> new Tuple<>(leftValue, rightValue)));
    }
}
