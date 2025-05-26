package magma.app.compile.rule;

import magma.api.option.Option;

public interface Rule<T> {
    Option<T> lex(String input);
}
