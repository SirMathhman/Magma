package magma.util;

import java.util.Objects;

/**
 * Simple generic tuple/pair type.
 */
public record Tuple<L, R>(L left, R right) {
    public Tuple {
        Objects.requireNonNull(left, "left must not be absent");
        Objects.requireNonNull(right, "right must not be absent");
    }

    public static <L, R> Tuple<L, R> of(L l, R r) {
        return new Tuple<>(l, r);
    }
}
