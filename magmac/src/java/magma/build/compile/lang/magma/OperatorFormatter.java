package magma.build.compile.lang.magma;

import magma.api.Tuple;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.lang.Visitor;
import magma.build.compile.error.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.parse.Node;

public class OperatorFormatter implements Visitor {
    @Override
    public Result<Tuple<Node, State>, Error_> postVisit(Node node, State state) {
        if (node.findType().endsWith("-operator")) {
            return new Ok<>(new Tuple<>(node
                    .withString("after-left", " ")
                    .withString("after-operator", " "), state));
        }

        return new Ok<>(new Tuple<>(node, state));
    }
}
