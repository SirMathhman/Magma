package com.meti.state;

import com.meti.safe.NativeString;
import com.meti.safe.option.Option;

import java.util.function.Function;

public abstract class State {
    public final Stack stack;

    public State(Stack stack) {
        this.stack = stack;
    }

    public State mapStack(Function<Stack, Stack> mapper) {
        return copy(mapper.apply(stack));
    }

    protected abstract State copy(Stack stack);

    public abstract State mapValue(Function<NativeString, NativeString> mapper);

    public abstract Option<NativeString> findValue();

    public abstract PresentState withValue(NativeString value);

    public State empty() {
        return new EmptyState(stack);
    }

    public State enter() {
        return copy(stack.enter());
    }

    public State exit() {
        return copy(stack.exit());
    }
}
