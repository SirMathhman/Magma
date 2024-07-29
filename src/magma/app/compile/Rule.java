package magma.app.compile;

import magma.api.Result;

public interface Rule {
    Result<Node, CompileException> parse(String input);

    Result<String, CompileException> generate(Node node);
}
