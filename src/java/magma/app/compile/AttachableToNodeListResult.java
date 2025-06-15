package magma.app.compile;

import java.util.List;

public interface AttachableToNodeListResult<Node, Error> {
    NodeListResult<Node, Error, NodeResult<Node, Error>> attachTo(List<Node> list);
}
