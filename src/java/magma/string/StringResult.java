package magma.string;

import magma.option.Option;

import java.util.function.Supplier;

public interface StringResult {
    StringResult appendSlice(String slice);

    Option<String> toOption();

    StringResult prepend(String slice);

    StringResult appendResult(Supplier<StringResult> other);
}
