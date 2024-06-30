package magma.build.compile.lang.java;

import magma.api.Tuple;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.lang.Visitor;
import magma.build.compile.parse.Node;

import java.util.function.BiFunction;

public record FilteringVisitor(String type, Visitor child) implements Visitor {
    @Override
    public Result<Tuple<Node, State>, Error_> preVisit(Node node, State state) {
        return applyFilter(node, state, child::preVisit);
    }

    private Result<Tuple<Node, State>, Error_> applyFilter(Node node, State state, BiFunction<Node, State, Result<Tuple<Node, State>, Error_>> folder) {
        if (!node.is(type)) return new Ok<>(new Tuple<>(node, state));
        return folder.apply(node, state);
    }

    @Override
    public Result<Tuple<Node, State>, Error_> postVisit(Node node, State state) {
        return applyFilter(node, state, child::postVisit);
    }
}
