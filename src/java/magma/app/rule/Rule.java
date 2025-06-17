package magma.app.rule;

import magma.CompileError;
import magma.api.Result;
import magma.app.node.Node;

public interface Rule {
    Result<Node, CompileError> lex(String input);

    Result<String, CompileError> generate(Node node);
}
