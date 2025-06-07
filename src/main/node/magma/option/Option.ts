/**
 * Minimal optional value container with distinct variants.
 */
export interface Option<T> {
    boolean isSome();

    T get();
}
