import Iter from "../list/Iter";
/** Holds a present value. */
export default class Some<T> implements Option<T> {
    private readonly value: T;

    Some(value: T): public {
        // TODO
    }

    @Override
    isSome(): boolean {
        return /* TODO */;
    }

    @Override
    get(): T {
        return value;
    }

    @Override
    toIter(): Iter<T> {
        return new OptionIter<T>(this);
    }
}
