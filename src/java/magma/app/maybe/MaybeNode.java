package magma.app.maybe;

import magma.app.Node;

import java.util.List;

public interface MaybeNode extends Attachable<Node> {
    MaybeNodeList addTo(List<Node> list);
}
