package magma.app.compile;

import magma.api.Tuple;

import java.util.Optional;

public interface Visitor {
    Optional<Tuple<Node, Integer>> postVisit(Node node, int state);

    Optional<Tuple<Node, Integer>> preVisit(Node node, int state);
}
