package magma.app.maybe;

import magma.app.Node;

import java.util.List;

public interface NodeResult extends Attachable<Node> {
    NodeListResult addTo(List<Node> list);
}
