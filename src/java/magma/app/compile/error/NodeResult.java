package magma.app.compile.error;

import magma.api.Result;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node, Error, StringResult> extends AttachableToState<Node, Error> {
    NodeResult<Node, Error, StringResult> transform(Function<Node, NodeResult<Node, Error, StringResult>> mapper);

    StringResult generate(Function<Node, StringResult> generator);

    Result<List<Node>, Error> attachToList(List<Node> nodes);
}
