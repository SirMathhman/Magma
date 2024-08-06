package magma.app.compile;

import magma.api.Tuple;

public interface ModifyingStage {
    Tuple<Node, Integer> modify(Node node, int depth);
}
