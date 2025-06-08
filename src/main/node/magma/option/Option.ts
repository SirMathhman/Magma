import Iter from "../list/Iter";
/**
 * Minimal optional value container with distinct variants.
 */
export interface Option<T> {
    isSome(): boolean;

    get(): T;

    toIter(): Iter<T>;
}
