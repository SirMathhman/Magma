package magma.node.result;

import magma.error.FormattedError;
import magma.result.Result;

import java.util.function.Function;

public interface NodeResult<Node> extends Matching<Node> {
    <Return> Result<Return, FormattedError> mapToResult(Function<Node, Return> mapper);

    NodeResult<Node> map(Function<Node, Node> mapper);
}
