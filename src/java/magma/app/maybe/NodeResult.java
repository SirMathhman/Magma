package magma.app.maybe;

import java.util.List;

public interface NodeResult<Node> extends Attachable<Node> {
    NodeListResult<Node> addTo(List<Node> list);
}
