package magma.result;

import magma.option.Option;

/**
 * Simple result type for success or error with distinct variants.
 */
public interface Result<T> {
    boolean isOk();

    Option<T> value();

    Option<String> error();
}
