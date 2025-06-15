package magma.app.maybe.node;

import magma.app.Node;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeNodeList;

import java.util.List;

public record PresentNode(Node node) implements MaybeNode<MaybeNodeList> {

    @Override
    public MaybeNodeList addTo(List<Node> list) {
        list.add(this.node);
        return new PresentNodeList(list);
    }
}
