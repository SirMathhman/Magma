package com.meti.compile.common.condition;

import com.meti.compile.attribute.Attribute;
import com.meti.compile.attribute.AttributeException;
import com.meti.compile.attribute.NodeAttribute;
import com.meti.compile.node.Node;

import java.util.stream.Stream;

public record Condition(Type type, Node state, Node body) implements Node {
    @Override
    public Stream<Attribute.Type> apply(Attribute.Group group) throws AttributeException {
        return group == Attribute.Group.Node
                ? Stream.of(Attribute.Type.Arguments, Attribute.Type.Value)
                : Stream.empty();
    }

    @Override
    public Attribute apply(Attribute.Type type) throws AttributeException {
        return switch (type) {
            case Arguments -> new NodeAttribute(state);
            case Value -> new NodeAttribute(body);
            default -> throw new AttributeException(type);
        };
    }

    @Override
    public boolean is(Type type) {
        return this.type == type;
    }

    @Override
    public Node with(Attribute.Type type, Attribute attribute) throws AttributeException {
        return switch (type) {
            case Arguments -> new Condition(this.type, attribute.asNode(), body);
            case Value -> new Condition(this.type, state, attribute.asNode());
            default -> this;
        };
    }
}