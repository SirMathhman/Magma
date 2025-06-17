package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.CompileError;
import magma.app.compile.node.Node;

public interface Rule {
    Result<Node, CompileError> lex(String input);

    Result<String, CompileError> generate(Node node);
}
