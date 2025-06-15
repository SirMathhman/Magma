package magma.app.maybe;

import java.util.List;
import java.util.function.Function;

public interface NodeListResult<Node> {
    NodeListResult<Node> add(NodeResult<Node> node);

    NodeListResult<Node> transform(Function<List<Node>, List<Node>> mapper);

    StringResult generate(Function<List<Node>, StringResult> generator);
}
