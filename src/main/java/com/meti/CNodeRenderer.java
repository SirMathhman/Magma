package com.meti;

import com.meti.node.Node;

public class CNodeRenderer extends CompoundProcessor<String> {
    private final Node value;

    public CNodeRenderer(Node value) {
        this.value = value;
    }

    @Override
    protected Stream<Processor<String>> stream() {
        return new ArrayStream<>(
                new BlockRenderer(value),
                new FunctionRenderer(value),
                new IntegerRenderer(value),
                new ReturnRenderer(value),
                new SequenceRenderer(value));
    }
}
