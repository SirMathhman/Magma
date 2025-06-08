import BiFunction from "../../java/util/function/BiFunction";
import Function from "../../java/util/function/Function";
/** Generic iterator independent of collection type. */
export interface Iter<T> {
    boolean hasNext();
    T next();

    map(fn: R>): ListLike<R> {
        let result: ListLike<R> = JdkList.create();
        while (hasNext()) {
            result.add(fn.apply(next()));
        }
        return result;
    }

    flatMap(fn: Iter<R>>): ListLike<R> {
        return fold(JdkList.create(),(acc, value);
        fn.apply(value).fold(acc,(a, r);
        a.add(r);
        return a;
        // TODO
        return acc;
        // TODO
    }

    fold(init: R, fn: R>): R {
        let acc: var = init;
        while (hasNext()) {
            // TODO
        }
        return acc;
    }
}
