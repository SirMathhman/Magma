package magma.compile.rule;

import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Result;

public interface Rule {
	Result<Node, CompileError> lex(String content);

	Result<String, CompileError> generate(Node node);
}
