package magma.app.compile.rule.action;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.error.NodeErr;
import magma.app.compile.error.NodeOk;
import magma.app.compile.error.NodeResult;

import java.util.Optional;

public class CompileResults {
    public static <Node> NodeResult<Node> fromOptionWithString(Optional<Node> option, String input) {
        return option.<NodeResult<Node>>map(NodeOk::new)
                .orElseGet(() -> new NodeErr<>(new CompileError()));
    }

    public static <Node> Result<String, CompileError> fromOptionWithNode(Optional<String> option, Node node) {
        return option.<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError()));
    }
}
