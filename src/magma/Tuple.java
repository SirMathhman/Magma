package magma;

import java.util.Objects;

final class Tuple<L, R> {
    public final L left;
    public final R right;

    Tuple(L left, R right) {
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
