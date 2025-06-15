package magma.app;

import java.util.List;

public interface AttachableToNodeListResult<Node, Error> {
    NodeListResult<Node, Error> attachTo(List<Node> list);
}
