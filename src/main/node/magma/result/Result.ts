import Option from "../option/Option";
/**
 * Simple result type for success or error with distinct variants.
 */
export interface Result<T> {
    isOk(): boolean;

    value(): Option<T>;

    error(): Option<string>;
}
