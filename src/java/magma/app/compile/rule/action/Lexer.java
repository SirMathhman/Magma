package magma.app.compile.rule.action;

import magma.api.Result;

public interface Lexer<Node> {
    Result<Node, CompileError> lex(String input);
}
