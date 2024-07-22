package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.CompileException;

public interface Rule {
    Result<Node, CompileException> parse(String input);

    Result<String, CompileException> generate(Node node);
}
