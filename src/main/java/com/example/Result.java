package com.example;

/**
 * Simple result type for success or error.
 */
public final class Result<T> {
    private final T ok;
    private final String err;

    private Result(T ok, String err) {
        this.ok = ok;
        this.err = err;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(null, message);
    }

    public boolean isOk() {
        return err == null;
    }

    public Option<T> value() {
        return isOk() ? Option.some(ok) : Option.none();
    }

    public Option<String> error() {
        return isOk() ? Option.none() : Option.some(err);
    }
}
