package magma.app.compile.rule.action;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public class CompileResults {
    public static <Node> Result<Node, CompileError> fromOptionWithString(Optional<Node> option, String input) {
        return option.<Result<Node, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError()));
    }

    public static <Node> Result<String, CompileError> fromOptionWithNode(Optional<String> option, Node node) {
        return option.<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError()));
    }
}
