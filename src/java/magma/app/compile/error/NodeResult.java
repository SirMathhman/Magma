package magma.app.compile.error;

import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node, Error> {
    NodeResult<Node, Error> transform(Function<Node, NodeResult<Node, Error>> mapper);

    StringResult<Error> generate(Function<Node, StringResult<Error>> generator);

    OrState<Node, Error> attachToState(OrState<Node, Error> nodeState);

    Result<List<Node>, Error> attachToList(List<Node> nodes);
}
