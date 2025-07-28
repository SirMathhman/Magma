package magma.rule;

import magma.CompileError;
import magma.node.Node;
import magma.result.Result;

public interface Rule {
	Result<Node, CompileError> lex(String input);

	Result<String, CompileError> generate(Node node);
}
