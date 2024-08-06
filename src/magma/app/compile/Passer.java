package magma.app.compile;

import magma.api.Tuple;

import java.util.Optional;

public interface Passer {
    default Optional<Tuple<Node, Integer>> postVisit(Node node, int state) {
        return Optional.empty();
    }

    default Optional<Tuple<Node, Integer>> preVisit(Node node, int state) {
        return Optional.empty();
    }
}
