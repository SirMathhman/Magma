package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;

import java.util.Optional;

public interface Passer {
    default Optional<Tuple<Node, Integer>> postVisit(Node node, int state) {
        return Optional.empty();
    }

    default Optional<Tuple<Node, Integer>> preVisit(Node node, int state) {
        return Optional.empty();
    }
}
