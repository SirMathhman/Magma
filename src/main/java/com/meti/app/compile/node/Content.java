package com.meti.app.compile.node;

import com.meti.api.collect.java.List;
import com.meti.app.compile.attribute.Attribute;
import com.meti.app.compile.attribute.AttributeException;
import com.meti.app.compile.attribute.TextAttribute;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Content(Text text) implements Node {
    @Override
    public Attribute apply(Attribute.Type type) throws AttributeException {
        if (type == Attribute.Type.Value) return new TextAttribute(getValueAsString());
        throw new AttributeException("No attribute exists of name: " + type);
    }

    @Override
    @Deprecated
    public Stream<Attribute.Type> apply(Attribute.Group group) throws AttributeException {
        return Stream.empty();
    }

    @Override
    public com.meti.api.collect.stream.Stream<Attribute.Type> apply1(Attribute.Group group) throws AttributeException {
        return List.createList(apply(group).collect(Collectors.toList())).stream();
    }

    private Text getValueAsString() {
        return text;
    }

    @Override
    public boolean is(Type type) {
        return type == Node.Type.Content;
    }
}
