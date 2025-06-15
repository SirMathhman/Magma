package magma.app.maybe.node;

import magma.app.Generated;
import magma.app.Generator;
import magma.app.Node;
import magma.app.maybe.MaybeNode;
import magma.app.result.EmptyGenerated;

import java.util.List;
import java.util.stream.Stream;

public class EmptyNode implements MaybeNode {
    @Override
    public MaybeNode withString(String key, String value) {
        return this;
    }

    @Override
    public Generated generate(Generator generator) {
        return new EmptyGenerated();
    }

    @Override
    public Stream<Node> stream() {
        return Stream.empty();
    }

    @Override
    public List<Node> addTo(List<Node> list) {
        return list;
    }
}
