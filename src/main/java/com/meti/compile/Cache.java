package com.meti.compile;

import com.meti.compile.attribute.Attribute;
import com.meti.compile.attribute.AttributeException;
import com.meti.compile.attribute.NodeAttribute;
import com.meti.compile.attribute.NodesAttribute;
import com.meti.compile.node.Node;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Cache(Node value, List<Node> children) implements Node {
    @Override
    public Stream<Attribute.Type> apply(Attribute.Group group) throws AttributeException {
        return switch (group) {
            case Node -> Stream.of(Attribute.Type.Value);
            case Nodes -> Stream.of(Attribute.Type.Children);
            default -> Stream.empty();
        };
    }

    @Override
    public Attribute apply(Attribute.Type type) throws AttributeException {
        return switch (type) {
            case Value -> new NodeAttribute(value);
            case Children -> new NodesAttribute(children);
            default -> throw new AttributeException(type);
        };
    }

    @Override
    public boolean is(Type type) {
        return type == Type.Cache;
    }

    @Override
    public Node with(Attribute.Type type, Attribute attribute) throws AttributeException {
        return switch (type) {
            case Value -> new Cache(attribute.asNode(), children);
            case Children -> new Cache(value, attribute.asStreamOfNodes().collect(Collectors.toList()));
            default -> this;
        };
    }
}