package magma.app;

import java.util.List;
import java.util.function.Function;

public interface NodeListResult<Node, Error> {
    NodeListResult<Node, Error> add(AttachableToNodeListResult<Node, Error> node);

    NodeListResult<Node, Error> transform(Function<List<Node>, List<Node>> mapper);

    StringResult<Error> generate(Function<List<Node>, StringResult<Error>> generator);
}
