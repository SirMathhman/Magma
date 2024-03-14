package com.meti.compile.attempt;

import com.meti.collect.option.Option;
import com.meti.collect.option.Some;
import com.meti.compile.node.Node;

public class TryNode implements Node {
    private final Node content;

    public TryNode(Node content) {
        this.content = content;
    }

    @Override
    public Option<String> render() {
        return content.render().map(value -> "try" + value);
    }

    @Override
    public Option<Node> withValue(Node value) {
        return Some.Some(new TryNode(value));
    }

    @Override
    public Option<Node> findValueAsNode() {
        return Some.Some(content);
    }

    @Override
    public boolean is(String name) {
        return name.equals("try");
    }
}
