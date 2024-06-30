package magma.build.compile.lang.java;

import magma.api.Tuple;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.error.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.lang.Visitor;
import magma.build.compile.parse.Node;

public class LambdaNormalizer implements Visitor {
    @Override
    public Result<Tuple<Node, State>, Error_> preVisit(Node node, State state) {
        return new Ok<>(new Tuple<>(node.retype("function"), state));
    }
}
