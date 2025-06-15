package magma.app.maybe.node;

import magma.app.Generated;
import magma.app.Generator;
import magma.app.Node;
import magma.app.maybe.MaybeNode;
import magma.app.result.PresentGenerated;

import java.util.List;

public record PresentNode(Node node) implements MaybeNode {

    @Override
    public Generated generate(Generator generator) {
        return new PresentGenerated(generator.generate(this.node));
    }

    @Override
    public List<Node> addTo(List<Node> list) {
        list.add(this.node);
        return list;
    }
}
