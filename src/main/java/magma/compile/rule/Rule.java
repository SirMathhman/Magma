package magma.compile.rule;

import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Result;

public interface Rule {
	Result<Node, CompileError> lex(TokenSequence content);

	Result<TokenSequence, CompileError> generate(Node node);
}
