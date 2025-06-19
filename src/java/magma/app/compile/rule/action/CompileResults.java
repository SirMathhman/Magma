package magma.app.compile.rule.action;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public class CompileResults {
    public static <Node> Result<Node, CompileError> fromOption(Optional<Node> node, String input) {
        return node.<Result<Node, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError()));
    }
}
