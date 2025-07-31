package magma.rule;

import magma.node.Node;
import magma.result.Result;

public interface Rule {
	Result<String, String> generate(Node node);

	Result<Node, String> lex(String input);
}
