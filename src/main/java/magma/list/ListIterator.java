package magma.list;

import java.util.function.BiFunction;
import java.util.function.Function;
import magma.list.JdkList;

/** Minimal iterator abstraction independent of java.util.Iterator. */

public interface ListIterator<T> {
    boolean hasNext();
    T next();

    default <R> ListLike<R> map(Function<T, R> fn) {
        ListLike<R> result = JdkList.create();
        while (hasNext()) {
            result.add(fn.apply(next()));
        }
        return result;
    }

    default <R> ListLike<R> flatMap(Function<T, ListIterator<R>> fn) {
        return fold(JdkList.create(), (acc, value) -> {
            fn.apply(value).fold(acc, (a, r) -> {
                a.add(r);
                return a;
            });
            return acc;
        });
    }

    default <R> R fold(R init, BiFunction<R, T, R> fn) {
        var acc = init;
        while (hasNext()) {
            acc = fn.apply(acc, next());
        }
        return acc;
    }
}
