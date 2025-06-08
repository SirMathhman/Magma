import BiFunction from "../../java/util/function/BiFunction";
import Function from "../../java/util/function/Function";
import JdkList from "./JdkList";
/** Generic iterator independent of collection type. */
export interface Iter<T> {
    hasNext(): boolean;
    next(): T;

    map(fn: R>): ListLike<R> {
        let result : ListLike<R> = JdkList.create();
        while (hasNext()) {
            result.add(fn.apply(next()));
        }
        return result;
    }

    flatMap(fn: Iter<R>>): ListLike<R> {
        return fold(JdkList.create(), (acc, value) => {
            fn.apply(value).fold(acc, (a, r) => {
                a.add(r);
                return a;
            });
            return acc;
        });
    }

    fold(init: R, fn: R>): R {
        let acc : unknown = init;
        while (hasNext()) {
            // TODO
        }
        return acc;
    }
}
