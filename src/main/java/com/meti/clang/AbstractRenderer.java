package com.meti.clang;

import com.meti.compile.node.Node;
import com.meti.compile.node.output.Output;

public abstract class AbstractRenderer extends AbstractProcessor<Output> {
    protected final Node node;
    protected final Node.Type type;

    public AbstractRenderer(Node node, Node.Type type) {
        this.node = node;
        this.type = type;
    }

    @Override
    protected boolean validate() {
        return node.is(type);
    }
}
