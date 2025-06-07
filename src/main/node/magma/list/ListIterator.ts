import BiFunction from "../../java/util/function/BiFunction";
import Function from "../../java/util/function/Function";
import JdkList from "./JdkList";
/** Minimal iterator abstraction independent of java.util.Iterator. */

export interface ListIterator<T> {
    boolean hasNext();
    T next();

    map(Function<T: any, fn: R>): ListLike<R> {
        let result: ListLike<R> = JdkList.create();
        while (hasNext()) {
            result.add(fn.apply(next()));
        }
        return result;
    }

    fold(init: R, BiFunction<R: any, T: any, fn: R>): R {
        let acc: var = init;
        while (hasNext()) {
            // TODO
        }
        return acc;
    }
}
