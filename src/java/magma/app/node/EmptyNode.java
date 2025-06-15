package magma.app.node;

import magma.app.Generated;
import magma.app.Generator;
import magma.app.MaybeNode;
import magma.app.result.EmptyGenerated;

public class EmptyNode implements MaybeNode {
    @Override
    public MaybeNode withString(String key, String value) {
        return this;
    }

    @Override
    public Generated generate(Generator generator) {
        return new EmptyGenerated();
    }
}
