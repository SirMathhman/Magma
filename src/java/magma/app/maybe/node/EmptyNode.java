package magma.app.maybe.node;

import magma.app.Node;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeNodeList;
import magma.app.rule.OrState;

import java.util.List;

public class EmptyNode implements MaybeNode {
    @Override
    public MaybeNodeList addTo(List<Node> list) {
        return new EmptyNodeList();
    }

    @Override
    public OrState<Node> attachTo(OrState<Node> state) {
        return state;
    }
}
