package magma.rule;

import magma.Node;

import java.util.Optional;

public interface Rule {
	Optional<Node> lex(String input);

	Optional<String> generate(Node node);
}
