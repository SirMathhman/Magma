package magma.app.maybe;

import magma.app.Node;

import java.util.List;

public interface MaybeNode<MaybeNodeList> {
    MaybeNodeList addTo(List<Node> list);
}
