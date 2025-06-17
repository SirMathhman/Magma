package magma.app.compile.node;

import magma.api.result.Result;
import magma.app.compile.AttachableToStateResult;
import magma.app.compile.MergeNodeResult;
import magma.app.compile.TypeNodeResult;
import magma.app.compile.string.StringResult;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node, Error> extends MergeNodeResult<Node, NodeResult<Node, Error>>, TypeNodeResult<NodeResult<Node, Error>>, AttachableToStateResult<Node, Error> {
    Result<List<Node>, Error> appendTo(List<Node> list);

    StringResult generate(Function<Node, StringResult> mapper);

    NodeResult<Node, Error> transform(Function<Node, Node> transformer);
}