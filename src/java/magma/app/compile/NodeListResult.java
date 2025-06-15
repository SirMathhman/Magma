package magma.app.compile;

import java.util.List;
import java.util.function.Function;

public interface NodeListResult<Node, Error, Result> {
    NodeListResult<Node, Error, NodeResult<Node, Error>> add(AttachableToNodeListResult<Node, Error> node);

    NodeListResult<Node, Error, NodeResult<Node, Error>> transform(Function<List<Node>, List<Node>> mapper);

    StringResult<Error> generate(Function<List<Node>, StringResult<Error>> generator);

    Result toNode(String key);
}
