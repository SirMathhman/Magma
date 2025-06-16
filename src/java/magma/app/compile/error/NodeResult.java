package magma.app.compile.error;

import magma.api.Result;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node, Error> extends AttachableToState<Node, Error> {
    NodeResult<Node, Error> transform(Function<Node, NodeResult<Node, Error>> mapper);

    StringResult<Error> generate(Function<Node, StringResult<Error>> generator);

    Result<List<Node>, Error> attachToList(List<Node> nodes);
}
