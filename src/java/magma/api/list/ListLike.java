package magma.api.list;

import java.util.function.BiFunction;

public interface ListLike<T> extends Sequence<T> {
    ListLike<T> add(T element);

    ListLike<T> addAll(Foldable<T> others);

    @Override
    default <R> R fold(R initial, BiFunction<R, T, R> folder) {
        R current = initial;
        for (var i = 0; i < this.size(); i++) {
            final var other = this.get(i);
            current = folder.apply(current, other);
        }
        return current;
    }
}
