package magma.node.result;

import magma.error.FormattedError;
import magma.node.EverythingNode;
import magma.result.Result;

import java.util.function.Function;

public interface NodeResult extends Matching<EverythingNode> {
    <Return> Result<Return, FormattedError> mapToResult(Function<EverythingNode, Return> mapper);

    NodeResult map(Function<EverythingNode, EverythingNode> mapper);
}
