package com.example.result;

import com.example.option.None;
import com.example.option.Option;
import com.example.option.Some;

/** Successful result value. */
public final class Ok<T> implements Result<T> {
    private final T value;

    Ok(T value) {
        this.value = value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public Option<T> value() {
        return new Some<>(value);
    }

    @Override
    public Option<String> error() {
        return new None<>();
    }
}
