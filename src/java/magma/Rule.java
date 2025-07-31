package magma;

import magma.node.Node;

import java.util.Optional;

public interface Rule {
	Optional<String> generate(Node node);
}
