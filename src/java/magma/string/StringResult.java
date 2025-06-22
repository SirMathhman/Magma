package magma.string;

import magma.option.Option;

public interface StringResult extends Appending<StringResult> {
    Option<String> toOption();

    StringResult prepend(String slice);
}
