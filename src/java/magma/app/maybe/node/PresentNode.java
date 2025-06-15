package magma.app.maybe.node;

import magma.app.Generated;
import magma.app.Generator;
import magma.app.Node;
import magma.app.maybe.MaybeNode;
import magma.app.result.PresentGenerated;

public record PresentNode(Node node) implements MaybeNode {
    @Override
    public MaybeNode withString(String key, String value) {
        return new PresentNode(this.node.withString(key, value));
    }

    @Override
    public Generated generate(Generator generator) {
        return new PresentGenerated(generator.generate(this.node));
    }
}
