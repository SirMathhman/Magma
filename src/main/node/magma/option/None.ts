import Iter from "../list/Iter";
/** Represents the absence of a value. */
export default class None<T> implements Option<T> {
    None(): public {
        // TODO
    }

    @Override
    isSome(): boolean {
        return /* TODO */;
    }

    @Override
    get(): T {
        return /* TODO */;
    }

    @Override
    toIter(): Iter<T> {
        return new OptionIter<T>(this);
    }
}
