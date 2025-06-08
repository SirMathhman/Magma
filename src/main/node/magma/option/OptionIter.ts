import Iter from "../list/Iter";
export default class OptionIter<T> implements Iter<T> {
    private readonly self: Option<T>;
    private done: boolean;

    OptionIter(self: Option<T>): public {
        // TODO
        // TODO
    }

    @Override
    hasNext(): boolean {
        return !done;
    }

    @Override
    next(): T {
        // TODO
        return self.get();
    }
}
