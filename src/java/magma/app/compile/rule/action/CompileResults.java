package magma.app.compile.rule.action;

import magma.app.compile.error.node.NodeErr;
import magma.app.compile.error.node.NodeOk;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringOk;
import magma.app.compile.error.string.StringResult;

import java.util.Optional;

public class CompileResults {
    public static <Node> NodeResult<Node> fromOptionWithString(Optional<Node> option, String input) {
        return option.<NodeResult<Node>>map(NodeOk::new)
                .orElseGet(() -> new NodeErr<>(new CompileError()));
    }

    public static <Node> StringResult fromOptionWithNode(Optional<String> option, Node node) {
        return option.<StringResult>map(StringOk::new)
                .orElseGet(() -> new StringErr(new CompileError()));
    }
}
