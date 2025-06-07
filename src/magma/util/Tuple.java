package magma.util;

import java.util.Objects;

public final class Tuple<L, R> {
    public final L left;
    public final R right;

    public Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }
}
