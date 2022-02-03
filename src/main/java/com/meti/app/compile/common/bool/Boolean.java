package com.meti.app.compile.common.bool;

import com.meti.api.collect.java.List;
import com.meti.api.json.JSONNode;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.attribute.Attribute;
import com.meti.app.compile.node.attribute.AttributeException;
import com.meti.app.compile.node.attribute.BooleanAttribute;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Boolean implements Node {
    True(true),
    False(false);

    private final boolean state;


    Boolean(boolean state) {
        this.state = state;
    }

    @Override
    public Attribute apply(Attribute.Category category) throws AttributeException {
        if (category == Attribute.Category.Value) return new BooleanAttribute(state);
        throw new AttributeException(category);
    }

    @Deprecated
    private Stream<Attribute.Category> apply2(Attribute.Group group) throws AttributeException {
        return Stream.empty();
    }

    @Override
    public com.meti.api.collect.stream.Stream<Attribute.Category> apply(Attribute.Group group) throws AttributeException {
        return List.createList(apply2(group).collect(Collectors.toList())).stream();
    }

    @Override
    public boolean is(Category category) {
        return category == Node.Category.Boolean;
    }

    @Override
    public JSONNode toJSON() {
        throw new UnsupportedOperationException();
    }
}
