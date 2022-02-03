package com.meti.app.compile.common;

import com.meti.api.collect.java.List;
import com.meti.api.collect.stream.Stream;
import com.meti.api.collect.stream.Streams;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.Type;
import com.meti.app.compile.node.attribute.Attribute;
import com.meti.app.compile.node.attribute.AttributeException;
import com.meti.app.compile.node.attribute.NodeAttribute;
import com.meti.app.compile.text.Input;
import com.meti.app.compile.text.RootText;

public class Initialization extends Definition {
    private final Node value;

    public Initialization(String name, Type type, Node value, Flag... flags) {
        this(new RootText(name), type, value, flags);
    }

    public Initialization(Input name, Type type, Node value, Flag... flags) {
        this(name, type, value, List.apply(flags));
    }

    public Initialization(Input name, Type type, Node value, List<Flag> flags) {
        super(flags, name, type);
        this.value = value;
    }

    @Override
    public Attribute apply(Attribute.Category category) throws AttributeException {
        return category == Attribute.Category.Value ? new NodeAttribute(value) : super.apply(category);
    }

    @Override
    public Stream<Attribute.Category> apply(Attribute.Group group) throws AttributeException {
        return group == Attribute.Group.Node ? Streams.apply(Attribute.Category.Value) : Streams.empty();
    }

    @Override
    public Node with(Attribute.Category category, Attribute attribute) throws AttributeException {
        return category == Attribute.Category.Value
                ? new Initialization(name, this.type, attribute.asNode(), flags)
                : super.with(category, attribute);
    }

    @Override
    protected Definition complete(Input name, Type type) {
        return new Initialization(this.name, type, value, flags);
    }

    @Override
    public boolean is(Category category) {
        return category == Node.Category.Initialization;
    }
}
