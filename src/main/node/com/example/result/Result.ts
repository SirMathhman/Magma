import Option from "../option/Option";
/**
 * Simple result type for success or error with distinct variants.
 */
export interface Result<T> {
    boolean isOk();

    Option<T> value();

    Option<String> error();
}
