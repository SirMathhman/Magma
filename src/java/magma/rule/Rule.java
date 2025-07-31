package magma.rule;

import magma.node.Node;

import java.util.Optional;

public interface Rule {
	Optional<String> generate(Node node);
	
	Optional<Node> lex(String input);
}
