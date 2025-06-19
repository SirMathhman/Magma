package magma.app.compile.rule.action;

import magma.app.compile.error.node.NodeErr;
import magma.app.compile.error.node.NodeOk;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringOk;
import magma.app.compile.error.string.StringResult;

public class CompileResults {
    public static <Node> NodeResult<Node> fromNodeValue(Node node) {
        return new NodeOk<>(node);
    }

    public static <Node> NodeResult<Node> fromNodeError(String input, String message0) {
        return new NodeErr<>(new CompileError(message0, input));
    }

    public static <Node> StringResult fromStringError(Node node) {
        return new StringErr(new CompileError("Invalid value", node.toString()));
    }

    public static StringResult fromStringValue(String value) {
        return new StringOk(value);
    }
}
