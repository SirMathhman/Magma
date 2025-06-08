/** Minimal list abstraction mirroring java.util.List but without Iterable. */
export interface ListLike<T> {
    add(value : T): void;
    get(index : number): T;
    set(index : number, value : T): void;
    size(): number;
    iterator(): ListIter<T>;
}
