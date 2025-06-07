package com.example.result;

import com.example.option.None;
import com.example.option.Option;
import com.example.option.Some;

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
        return new None<>();
    }

    @Override
    public Option<String> error() {
        return new Some<>(message);
    }
}
