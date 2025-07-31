package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Result;

public interface Rule {
	Result<String, CompileError> generate(Node node);

	Result<Node, CompileError> lex(String input);
}
