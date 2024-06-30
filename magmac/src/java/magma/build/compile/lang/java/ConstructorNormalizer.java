package magma.build.compile.lang.java;

import magma.api.Tuple;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.lang.Visitor;
import magma.build.compile.parse.Node;

public class ConstructorNormalizer implements Visitor {
    @Override
    public Result<Tuple<Node, State>, Error_> preVisit(Node node, State state) {
        return new Ok<>(new Tuple<>(node.retype("invocation"), state));
    }
}
