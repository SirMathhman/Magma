package com.meti;

import java.io.IOException;
import java.util.function.Consumer;

public interface Result<C> {
    void match(C acceptValue, Consumer<IOException> acceptError);
}
