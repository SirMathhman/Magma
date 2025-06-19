package magma.app.compile.rule.action;

import magma.api.Result;

public interface Generator<Node> {
    Result<String, CompileError> generate(Node node);
}
