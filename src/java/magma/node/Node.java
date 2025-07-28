package magma.node;

import java.util.List;
import java.util.Optional;

public interface Node {
	Node merge(Node other);

	Node retype(String type);

	boolean is(String type);

	Node withNodeList(String key, List<Node> values);

	Optional<List<Node>> findNodeList(String key);

	Properties strings();
}
