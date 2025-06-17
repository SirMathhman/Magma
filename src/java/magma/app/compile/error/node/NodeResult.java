package magma.app.compile.error.node;

import magma.api.result.Result;
import magma.app.compile.error.AttachableToStateResult;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.MergeNodeResult;
import magma.app.compile.error.TypeNodeResult;
import magma.app.compile.error.string.StringResult;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node> extends MergeNodeResult<Node, NodeResult<Node>>, TypeNodeResult<NodeResult<Node>>, AttachableToStateResult<Node> {
    Result<List<Node>, FormattedError> appendTo(List<Node> list);

    StringResult generate(Function<Node, StringResult> mapper);

    NodeResult<Node> transform(Function<Node, Node> transformer);
}