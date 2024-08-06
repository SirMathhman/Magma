package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;

public interface ModifyingStage {
    Tuple<Node, Integer> modify(Node node, int depth);
}
