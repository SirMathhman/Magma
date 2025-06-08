/**
 * Minimal optional value container with distinct variants.
 */
export interface Option<T> {
    isSome(): boolean;

    get(): T;

    toIter(): magma.list.Iter<T> {
        let self : Option<T> = this;
        return new magma.list.Iter<T>() {;
        let done : boolean = !self.isSome();
        // TODO
        public boolean hasNext();
        return !done;
        // TODO
        // TODO
        public T next();
        // TODO
        return self.get();
        // TODO
        // TODO
    }
}
