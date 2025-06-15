package magma;

import magma.app.MaybeNodeList;
import magma.app.Node;
import magma.app.maybe.MaybeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PresentNodeList implements MaybeNodeList {
    private final List<Node> nodes;

    public PresentNodeList() {
        this(new ArrayList<>());
    }

    public PresentNodeList(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public MaybeNodeList add(MaybeNode node) {
        return new PresentNodeList(node.addTo(this.nodes));
    }

    @Override
    public MaybeNodeList transform(Function<List<Node>, List<Node>> mapper) {
        return new PresentNodeList(mapper.apply(this.nodes));
    }

    @Override
    public <Return> Return generate(Function<List<Node>, Return> generator) {
        return generator.apply(this.nodes);
    }
}
