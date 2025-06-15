package magma.app.maybe;

import magma.app.Node;

import java.util.List;
import java.util.function.Function;

public interface NodeListResult {
    NodeListResult add(NodeResult node);

    NodeListResult transform(Function<List<Node>, List<Node>> mapper);

    StringResult generate(Function<List<Node>, StringResult> generator);
}
