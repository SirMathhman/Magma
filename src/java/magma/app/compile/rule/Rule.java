package magma.app.compile.rule;

import magma.api.Result;
import magma.app.compile.CompileError;

public interface Rule<Node> {
    Result<Node, CompileError> lex(String input);

    Result<String, CompileError> generate(Node node);
}
