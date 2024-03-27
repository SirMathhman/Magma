package com.meti.rule;

import com.meti.Tuple;
import com.meti.node.Attribute;

import java.util.Map;
import java.util.Optional;

public class NamedRule implements Rule {
    private final Rule inner;
    private final String name;

    public NamedRule(String name, Rule inner) {
        this.inner = inner;
        this.name = name;
    }

    @Override
    public Optional<String> render(Map<String, Attribute> attributes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Tuple<Optional<String>, Map<String, Attribute>>> lexImpl(String input) {
        var result = inner.lexImpl(input).map(value -> value.replaceLeft(Optional.of(name)));
        return result;
    }
}
