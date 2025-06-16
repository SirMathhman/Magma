package magma.app.compile.error;

import java.util.List;

public interface CompileResultFactory<Node> {
    CompileResult<Node> fromNode(Node node);

    CompileResult<String> fromString(String generated);

    CompileResult<String> fromNodeError(String message, Node context);

    CompileResult<Node> fromStringError(String message, String context);

    CompileResult<List<Node>> fromEmptyNodeList();

    CompileResult<String> fromEmptyString();
}