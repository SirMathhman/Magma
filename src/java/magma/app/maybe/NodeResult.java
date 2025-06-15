package magma.app.maybe;

import java.util.List;

public interface NodeResult<Node, Error> extends Attachable<Node, Error> {
    NodeListResult<Node, Error> addTo(List<Node> list);
}
