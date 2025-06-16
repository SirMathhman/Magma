package magma.app.compile.rule;

import magma.app.compile.CompileResult;

public interface Rule<Node> {
    CompileResult<Node> lex(String input);

    CompileResult<String> generate(Node node);
}
