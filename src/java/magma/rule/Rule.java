package magma.rule;

import java.util.Optional;

public interface Rule<Node> {
	Optional<Node> lex(String input);

	Optional<String> generate(Node node);
}