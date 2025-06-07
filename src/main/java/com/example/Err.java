package com.example;

/** Error result with a message. */
public final class Err<T> implements Result<T> {
    private final String message;

    Err(String message) {
        this.message = message;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public Option<T> value() {
        return Option.none();
    }

    @Override
    public Option<String> error() {
        return Option.some(message);
    }
}
