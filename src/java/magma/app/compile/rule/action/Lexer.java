package magma.app.compile.rule.action;

import magma.api.Result;
import magma.app.compile.error.CompileError;

public interface Lexer<Node> {
    Result<Node, CompileError> lex(String input);
}
